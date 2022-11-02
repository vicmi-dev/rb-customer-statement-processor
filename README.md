# rb-customer-statement-processor

   
Spring Boot project which allows user to upload CSV and XML files with the predefined data format. 

 * It will validate whether the records include duplicated reference number, within the same file and against the historical data from the DB.
 * It will also validate within each transaction whether the end balance matches the difference between the start balance and the mutation.

## Assignment

A bank receives monthly deliveries of customer statement records. This information is delivered in two formats, CSV and XML. These records need to be validated based on the following conditions:

 * All transaction references should be unique
 * End balance needs to be validated 
 * Return a report with reference number and description of each of the failed transactions
    
## Quick Start

 * Clone this repository
 * Run `mvn clean package`
 * Run `mvn spring-boot:run`
 
## Alternative

 * Import the project in your favourite IDE
 * Run the file `"src\main\java\com\manuel\rb\RbCustomerStatementProcessorApplication.java"`
 
 ## Quick run
 
 * The API can be reached at http://localhost:8080/customer/api/upload
 * Upload the csv or xml files as 'files' Key in form-data POST body
 * The response will be a list/s of JSON objects including the reference number and description for each transaction with duplicated reference numbers or balance error.
 
