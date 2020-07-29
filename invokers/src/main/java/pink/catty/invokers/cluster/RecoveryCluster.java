/*
 * Copyright 2019 The Catty Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pink.catty.invokers.cluster;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import pink.catty.core.invoker.Consumer;
import pink.catty.core.invoker.frame.Request;
import pink.catty.core.invoker.frame.Response;
import pink.catty.core.meta.ClusterMeta;
import pink.catty.core.meta.ConsumerMeta;
import pink.catty.core.service.HealthCheckException;
import pink.catty.core.support.ConcurrentHashSet;
import pink.catty.core.utils.HeartBeatUtils;

public class RecoveryCluster extends FailOverCluster {

  private static final String TIMER_NAME = "CATTY_RECOVERY";
  private static final Set<ConsumerMeta> ON_RECOVERY;
  private static Timer TIMER;

  static {
    TIMER = new Timer(TIMER_NAME);
    ON_RECOVERY = new ConcurrentHashSet<>();
  }

  private int defaultRecoveryDelay;

  public RecoveryCluster(ClusterMeta clusterMeta) {
    super(clusterMeta);
    this.defaultRecoveryDelay = clusterMeta.getRecoveryPeriod();
  }

  @Override
  protected void processError(Consumer consumer, Request request, Throwable e) {
    final ConsumerMeta consumerMeta = consumer.getMeta();
    final String metaString = consumerMeta.toString();

    /*
     * Avoid duplicate recovery job.
     */
    synchronized (ON_RECOVERY) {
      if (ON_RECOVERY.contains(consumerMeta)) {
        logger.info(
            "Recovery job on this address was going on, new recovery job on this address would not be created again. address: {}",
            consumerMeta);
      } else {
        TIMER.schedule(
            new TimerTask() {
              @Override
              public void run() {

                /*
                 * 1. create a new invoker from provider info.
                 * 2. fire a heartbeat to endpoint, which will attempt to connect to endpoint.
                 * 3. if heartbeat succeed, register this invoker.
                 * 4. cancel this task.
                 */
                logger.info("Recovery: begin recovery of endpoint: {}", metaString);

                try {
                  Consumer newConsumer = getChainBuilder().buildConsumer(consumerMeta);
                  Request heartBeat = HeartBeatUtils.buildHeartBeatRequest(this);
                  String except = (String) heartBeat.getArgsValue()[0];
                  Response heartBeatResp = newConsumer.invoke(heartBeat);
                  heartBeatResp.await(defaultRecoveryDelay, TimeUnit.MILLISECONDS);
                  if (except.equals(heartBeatResp.getValue())) {
                    registerInvoker(metaString, newConsumer);
                    logger.info("Recovery: endpoint recovery succeed! endpoint: {}", metaString);
                    ON_RECOVERY.remove(consumerMeta);
                    cancel();
                  } else {
                    throw new HealthCheckException(
                        "Recovery: excepted: " + except + ", get: " + heartBeatResp.getValue());
                  }
                } catch (Exception e0) {
                  logger.info(
                      "Recovery: endpoint recovery failed, another try is going to begin, endpoint: {}",
                      metaString, e0);
                }
              }
            }, defaultRecoveryDelay, defaultRecoveryDelay);

        ON_RECOVERY.add(consumerMeta);
      }
    }
  }
}
