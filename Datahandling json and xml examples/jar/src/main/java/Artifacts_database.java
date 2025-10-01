import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

/**
 * This class processes historical artifacts data from a JSON file
 * and provides methods to analyze the data for historical research.
 */
@SuppressWarnings("unused")
public class Artifacts_database {
 
 /** The root node of the parsed JSON data */
 private JsonNode rootNode;

 /**
 * Constructor that initializes the database by loading JSON data from file
 *
 * @param jsonFilePath The path to the JSON file containing historical artifacts data
 */
 public Artifacts_database(String jsonFilePath) {
 try {
	 
 // Create Jackson ObjectMapper to parse the JSON file
 ObjectMapper mapper = new ObjectMapper();
 
 rootNode = mapper.readTree(new File(jsonFilePath));
 
 } catch (Exception e) {
 e.printStackTrace();
 }
 }

 /**
 * Analyzes the distribution of artifacts by century
 * 
 * @return A sorted map where keys are centuries and values are the number of artifacts from that century
 */
 public Map<String, Integer> getArtifactDistributionByCentury() {
 // Using TreeMap to automatically sort centuries chronologically
 Map<String, Integer> distribution = new TreeMap<>(); 
 
 // Navigate to the Artefacts array in the JSON structure
 JsonNode artefacts = rootNode.path("HistoricalArchive").path("Artefacts");
 
 if (artefacts.isArray()) {
 // Iterate through each artifact in the collection
 for (JsonNode artefact : artefacts) {
	 
 // Extract the date string from the current artifact
 String dateStr = artefact.path("DateOfCreation").asText();
 
 // Convert the date to a standardized century format
 String century = determineCentury(dateStr);
 
 if (!century.isEmpty()) {
	 
 // Increment the count for this century or initialize it to 1 if not present
 distribution.put(century, distribution.getOrDefault(century, 0) + 1);
 }
 }
 }
 
 return distribution;
 }
 
 /**
 * Determines the century from a date string, handling various formats
 * 
 * @param dateStr The date string to parse (e.g., "1850 CE", "300 BCE", "1798-1801 CE")
 * @return A formatted string representing the century (e.g., "19th Century CE", "3rd Century BCE")
 */
 private String determineCentury(String dateStr) {
	 
 // Return empty string for empty input
 if (dateStr.isEmpty()) {
 return "";
 }
 
 // Check if date is Before Common Era
 boolean isBCE = dateStr.contains("BCE");
 
 // Extract numeric characters only
 String yearStr = dateStr.replaceAll("[^0-9]", "");
 
 try {
	 
 // Parse the year as an integer
 int year = Integer.parseInt(yearStr);
 
 // Calculate century (year - 1) / 100 + 1
 // e.g., 1850 → 19th century, 2001 → 21st century
 int century = (year - 1) / 100 + 1;
 
 if (isBCE) {
	 
 // BCE dates use simpler "th" suffix for all centuries
 return century + "th Century BCE";
 } 
 else {
	 
 // CE dates use appropriate ordinal suffix (st, nd, rd, th)
 return century + getOrdinalSuffix(century) + " Century CE";
 }
 
 } catch (NumberFormatException e) {
	 
 // Handle date ranges like "1798-1801 CE" by using the start year
 try {
	 
 // Split the range and use the first part
 String[] parts = dateStr.split("-");
 
 // Extract the year from the first part
 int year = Integer.parseInt(parts[0].trim().replaceAll("[^0-9]", ""));
 
 // Calculate century as before
 int century = (year - 1) / 100 + 1;
 
 if (isBCE) {
 return century + "nd Century BCE";
 }
 else {
 return century + getOrdinalSuffix(century) + " Century CE";
 }
 
 } catch (Exception ex) {
	 
 // Log error for dates that couldn't be parsed
 System.err.println("Error parsing year from: " + dateStr);
 return "";
 }
 }
 }
 
 /**
 * Determines the correct ordinal suffix for a number (st, nd, rd, th)
 * 
 * @param num The number to get the suffix for
 * @return The appropriate ordinal suffix
 */
 
 //my attempt to get the right Suffix for each one lmao
 private String getOrdinalSuffix(int num) {
	 
 // Special case: 11th, 12th, 13th always use "th" suffix
 if (num % 100 == 11 || num % 100 == 12 || num % 100 == 13) {
 return "th";
 }
 
 // For other numbers, check the last digit
 switch (num % 10) {
 case 1: return "st"; // 1st, 21st, etc.
 case 2: return "nd"; // 2nd, 22nd, etc.
 case 3: return "rd"; // 3rd, 23rd, etc.
 default: return "th"; // 4th, 5th, etc.
 }
 }
 
 /**
 * Analyzes connections between historical individuals and different types of entities
 * 
 * @return A map where keys are individual names and values are maps of connection types and counts
 */
 
 public Map<String, Map<String, Integer>> getConnectionsByType() {
 Map<String, Map<String, Integer>> connectionsByType = new HashMap<>();
 
 // Navigate to the HistoricalIndividuals array in the JSON structure
 JsonNode individuals = rootNode.path("HistoricalArchive").path("HistoricalIndividuals");
 
 if (individuals.isArray()) {
	 
 // Iterate through each historical individual
 for (JsonNode individual : individuals) {
	 
 // Get the name of the current individual
 String name = individual.path("Name").asText();
 
 // Initialize a map to count different types of connections
 Map<String, Integer> typeCounts = new HashMap<>();
 
 // Navigate to the Connections object for this individual
 JsonNode connections = individual.path("Connections");
 
 // Count artifact references
 JsonNode artefactRefs = connections.path("ArtefactRefs");
 if (artefactRefs.isArray()) 
 {
 typeCounts.put("Artifact", artefactRefs.size());
 } 
 else {
 typeCounts.put("Artifact", 0);
 }
 
 // Count event references
 JsonNode eventRefs = connections.path("EventRefs");
 if (eventRefs.isArray()) {
 typeCounts.put("Event", eventRefs.size());
 } 
 else {
 typeCounts.put("Event", 0);
 }
 
 // Count individual references (connections to other people)
 JsonNode individualRefs = connections.path("IndividualRefs");
 
 if (individualRefs.isArray()) {
 typeCounts.put("Individual", individualRefs.size());
 } 
 else {
 typeCounts.put("Individual", 0);
 }
 
 // Store the counts for this individual
 connectionsByType.put(name, typeCounts);
 }
 }
 
 return connectionsByType;
 }
 
 /**
 * Main method to demonstrate the functionality of the Artifacts_database class
 * 
 * @param args Command line arguments (not used)
 */
 public static void main(String[] args) {
 // Initialize the processor with the example JSON file
 Artifacts_database processor = new Artifacts_database("example.json");
 
 // Question 1: What is the distribution of artifacts by century?
 System.out.println("Question 1: What is the distribution of artifacts by century?");
 
 // Get the distribution data
 Map<String, Integer> centuryDistribution = processor.getArtifactDistributionByCentury();
 
 // Print the results
 for (Map.Entry<String, Integer> entry : centuryDistribution.entrySet()) {
 System.out.println(entry.getKey() + ": " + entry.getValue() + " artifacts");
 }
 
 // Question 2: Which individuals have the most connections by type?
 System.out.println("\nQuestion 2: Which individuals have the most connections by type?");
 
 // Get the connections data
 Map<String, Map<String, Integer>> connectionsByType = processor.getConnectionsByType();
 
 // Print the results for each individual
 for (Map.Entry<String, Map<String, Integer>> entry : connectionsByType.entrySet()) {
 String name = entry.getKey();
 
 Map<String, Integer> types = entry.getValue();
 
 System.out.println(name + ":");
 System.out.println(" - Artifact connections: " + types.getOrDefault("Artifact", 0));
 System.out.println(" - Event connections: " + types.getOrDefault("Event", 0));
 System.out.println(" - Individual connections: " + types.getOrDefault("Individual", 0));
 
 // Calculate and print the total number of connections
 System.out.println(" - Total: " + (types.getOrDefault("Artifact", 0) + 
 types.getOrDefault("Event", 0) + 
 types.getOrDefault("Individual", 0)));
 }
 }
}