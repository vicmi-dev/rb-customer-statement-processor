# rb-customer-statement-processor

### Assignment ###

	A bank receives monthly deliveries of customer statement records. This information is delivered in two formats, CSV and XML. These transactions need to be validated based on the following conditions:
  
     * All transaction references should be unique
     * End balance needs to be validated 
     * Return a report with reference number and description of each of the failed transactions
     
     ## Quick Start

 * Clone this repository
 * Run `mvn clean package`
 * Run `mvn spring-boot:run`
 
     ##### Alternative

 * Import the project in your favourite IDE
 * Run the file `src/main/java/com/rabobank/statementprocessor/Application.java`
 
 ## Quick run
 
 * The API can be reached at http://localhost:8080/customer/api/upload
 * Upload the csv or xml files as 'files' attribute in POST body