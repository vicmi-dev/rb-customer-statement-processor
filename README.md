# rb-customer-statement-processor

### Assignment ###

	A bank receives monthly deliveries of customer statement records. This information is delivered in two formats, CSV and XML. These transactions need to be validated based on the following conditions:
   RB assignment to upload and validate CSV and XML data.


     * All transaction references should be unique
     * End balance needs to be validated 
     * Return a report with reference number and description of each of the failed transactions
     
     
    Spring Boot project which allows user to upload CSV and XML files with the predefined data format. 
    It will validate whether the records include duplicated reference number, within the same file and against the historical data from the DB.
    It will also validate within each transaction whether the end balance matches the difference between the start balance and the mutation.

    To test it, just clone the project, run it and test it on Postman attaching the files in form-data format, in the body of a POST request to the API http://localhost:8080/api/upload
    The key being "files" and the value the file itself.

    The response will be JSON objects including the transactions with duplicated reference numbers and balance error.
    
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
