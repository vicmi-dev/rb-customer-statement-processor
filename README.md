# rb-customer-statement-processor
 RB assignment to upload and validate CSV and XML data.

Spring Boot project which allows user to upload CSV and XML files with the predefined data format. 
It will validate whether the records include duplicated reference number, within the same file and against the historical data from the DB.
It will also validate within each transaction whether the end balance matches the difference between the start balance and the mutation.

To test it, just clone the project, run it and test it on Postman attaching the files in form-data format, in the body of a POST request to the API http://localhost:8080/api/upload
The key being "files" and the value the file itself.
