package com.yahoo.labs.samoa.topology.impl;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 Yahoo! Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.HashMap;
import java.util.Map;

import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.topology.ComponentFactory;
import com.yahoo.labs.samoa.topology.EntranceProcessingItem;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.topology.Topology;

/**
 * Component factory implementation for samoa-storm
 */
public final class StormComponentFactory implements ComponentFactory {

  private final Map<String, Integer> processorList;

  public StormComponentFactory() {
    processorList = new HashMap<>();
  }

  @Override
  public ProcessingItem createPi(Processor processor) {
    return new StormProcessingItem(processor, this.getComponentName(processor.getClass()), 1);
  }

  @Override
  public EntranceProcessingItem createEntrancePi(EntranceProcessor processor) {
    return new StormEntranceProcessingItem(processor, this.getComponentName(processor.getClass()));
  }

  @Override
  public Stream createStream(IProcessingItem sourcePi) {
    StormTopologyNode stormCompatiblePi = (StormTopologyNode) sourcePi;
    return stormCompatiblePi.createStream();
  }

  @Override
  public Topology createTopology(String topoName) {
    return new StormTopology(topoName);
  }

  private String getComponentName(Class<? extends Processor> clazz) {
    StringBuilder componentName = new StringBuilder(clazz.getCanonicalName());
    String key = componentName.toString();
    Integer index;

    if (!processorList.containsKey(key)) {
      index = 1;
    } else {
      index = processorList.get(key) + 1;
    }

    processorList.put(key, index);

    componentName.append('_');
    componentName.append(index);

    return componentName.toString();
  }

  @Override
  public ProcessingItem createPi(Processor processor, int parallelism) {
    return new StormProcessingItem(processor, this.getComponentName(processor.getClass()), parallelism);
  }
}
