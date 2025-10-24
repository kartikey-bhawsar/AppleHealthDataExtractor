package org.example;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) throws Exception {

        // Disable DTD validation and entity expansion limits
        System.setProperty("jdk.xml.totalEntitySizeLimit", "0");
        System.setProperty("jdk.xml.maxGeneralEntitySizeLimit", "0");
        System.setProperty("jdk.xml.entityExpansionLimit", "0");
        System.setProperty("jdk.xml.maxElementDepth", "0");
        System.setProperty("jdk.xml.maxParameterEntitySizeLimit", "0");
        System.setProperty("jdk.xml.maxOccurLimit", "0");
        System.setProperty("javax.xml.stream.isSupportingExternalEntities", "false");
        XMLInputFactory factory = XMLInputFactory.newInstance();
        // Disable DTD validation and external entities
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_VALIDATING, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream("/Users/kartik/Downloads/apple_health_export/export.xml"));
        Map<String, Integer> stepCountMap = new HashMap<>();
        Map<String, Integer> sortedStepCountMap = new TreeMap<>((k1, k2) -> {
            int compare = stepCountMap.get(k2).compareTo(stepCountMap.get(k1));
            if(compare == 0) return k1.compareTo(k2);
            return compare;
        });
        while (reader.hasNext()) {
            int event = reader.next();
            // 3️⃣ Check for start of "Record" tag
            if (event == XMLStreamConstants.START_ELEMENT && "Record".equals(reader.getLocalName())) {
                // Extract attributes
                String type = reader.getAttributeValue(null, "type");
                if ("HKQuantityTypeIdentifierStepCount".equals(type)) {
                    String startDate = reader.getAttributeValue(null, "startDate");
                    String value = reader.getAttributeValue(null, "value");
                    String date = startDate.split(" ")[0];
                    int stepCount = Integer.parseInt(value);
                    stepCountMap.put(date, stepCountMap.getOrDefault(date, 0) + stepCount);

                }
            }
        }
        sortedStepCountMap.putAll(stepCountMap);
        int k=1;
        for(Map.Entry<String, Integer> entry : sortedStepCountMap.entrySet()) {
            System.out.println(k+ " "+entry.getKey()+" "+entry.getValue());
            k++;
        }

        reader.close();
    }
}