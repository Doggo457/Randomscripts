package xml_Java;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlHandeling {
    
    private Document document;
    
    public XmlHandeling(String xmlFilePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            document = builder.parse(new File(xmlFilePath));
            
            document.getDocumentElement().normalize();
            
        } catch (Exception e) {
        	
            System.err.println("Error XML file unreadable: " + e.getMessage());
            e.printStackTrace();
            
        }
    }
    
    public static void main(String[] args) {
    	
        //the XML is saved to a file named "example"
        XmlHandeling parser = new XmlHandeling("example.xml");
        
        // Question 1: What is the distribution of artifacts by century?
        System.out.println("Question 1: What is the distribution of artifacts by century?");
        
        Map<String, Integer> centuryDistribution = parser.getArtifactDistributionByCentury();
        
        for (Map.Entry<String, Integer> entry : centuryDistribution.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " artifacts");
        }
        
        // Question 2: Which individuals have the most connections by type (artifacts, events, other individuals)?
        System.out.println("\nQuestion 2: Which individuals have the most connections by type?");
        
        Map<String, Map<String, Integer>> connectionsByType = parser.getConnectionsByType();
        
        for (Map.Entry<String, Map<String, Integer>> entry : connectionsByType.entrySet()) {
        	
            String name = entry.getKey();
            
            Map<String, Integer> types = entry.getValue();
            
            System.out.println(name + ":");
            System.out.println("  - Artifact connections: " + types.getOrDefault("Artifact", 0));
            System.out.println("  - Event connections: " + types.getOrDefault("Event", 0));
            System.out.println("  - Individual connections: " + types.getOrDefault("Individual", 0));
            System.out.println("  - Total: " + (types.getOrDefault("Artifact", 0) + 
                                             types.getOrDefault("Event", 0) + 
                                             types.getOrDefault("Individual", 0)));
        }
    }
    
    // Question 1: What is the distribution of artifacts by century?
    public Map<String, Integer> getArtifactDistributionByCentury()
    {
        Map<String, Integer> distribution = new TreeMap<>(); // TreeMap for sorted keys
        
        NodeList artifacts = document.getElementsByTagName("Artefact");
        
        for (int i = 0; i < artifacts.getLength(); i++) {
        	
            Element artifact = (Element) artifacts.item(i);
            String dateStr = getText(artifact, "DateOfCreation");
            String century = determineCentury(dateStr);
            
            if (!century.isEmpty()) {
                distribution.put(century, distribution.getOrDefault(century, 0) + 1);
            }
        }
        
        return distribution;
    }
    
    private String determineCentury(String dateStr) {
    	
        if (dateStr.isEmpty()) {
            return "";
        }
        
        boolean isBCE = dateStr.contains("BCE");
        
        String yearStr = dateStr.replaceAll("[^0-9]", "");
        
        try {
            int year = Integer.parseInt(yearStr);
            int century = (year - 1) / 100 + 1;
            
            if (isBCE) {
                return century + "th Century BCE";
            } 
            
            else {
                return century + getOrdinalSuffix(century) + " Century CE";
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing year from: " + dateStr);
            return "";
        }
    }
    
    private String getOrdinalSuffix(int num)
    
    {
        if (num % 100 == 11 || num % 100 == 12 || num % 100 == 13) {
            return "th";
        }
        
        switch (num % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }
    
    // Question 2: Which individuals have the most connections by type?
    public Map<String, Map<String, Integer>> getConnectionsByType() {
    	
        Map<String, Map<String, Integer>> connectionsByType = new HashMap<>();
        
        NodeList individuals = document.getElementsByTagName("Individual");
        
        for (int i = 0; i < individuals.getLength(); i++) {
        	
            Element individual = (Element) individuals.item(i);
            
            String name = getText(individual, "Name");
            
            Map<String, Integer> typeCounts = new HashMap<>();
            
            
            // Check if Connections element exists
            NodeList connections = individual.getElementsByTagName("Connections");
            
            if (connections.getLength() > 0)
            {
                Element connection = (Element) connections.item(0);
                
                // Count artifact references
                NodeList artifactRefsContainer = connection.getElementsByTagName("ArtefactRefs");
                
                if (artifactRefsContainer.getLength() > 0) {
                    NodeList artifactRefs = ((Element) artifactRefsContainer.item(0)).getElementsByTagName("ArtefactRef");
                    
                    typeCounts.put("Artifact", artifactRefs.getLength());
                }
                else {
                    typeCounts.put("Artifact", 0);
                }
                
                // Count event references
                NodeList eventRefsContainer = connection.getElementsByTagName("EventRefs");
                
                if (eventRefsContainer.getLength() > 0) {
                    NodeList eventRefs = ((Element) eventRefsContainer.item(0)).getElementsByTagName("EventRef");
                    
                    typeCounts.put("Event", eventRefs.getLength());
                } 
                else {
                    typeCounts.put("Event", 0);
                }
                
                // Count individual references
                NodeList individualRefsContainer = connection.getElementsByTagName("IndividualRefs");
                if (individualRefsContainer.getLength() > 0)
                {
                    NodeList individualRefs = ((Element) individualRefsContainer.item(0)).getElementsByTagName("IndividualRef");
                    typeCounts.put("Individual", individualRefs.getLength());
                } 
                else {
                    typeCounts.put("Individual", 0);
                }
            }
            
            connectionsByType.put(name, typeCounts);
        }
        
        return connectionsByType;
    }
    
    private String getText(Element element, String tagName)
    {
        NodeList nodeList = element.getElementsByTagName(tagName);
        
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
}