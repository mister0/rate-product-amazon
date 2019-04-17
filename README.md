### This repository contains a simple spring application (microservice) that uses amazon autocomplete API to estimate a score for how hot does this keyword have


The application receives get request with a keyword as a request parameter

#### Before running this application you need to make sure : 

* You have java installed from [here](https://java.com/en/download/help/index_installing.xml)
* You have maven installed from [here](https://maven.apache.org/install.html)

 To run the application : 
* To build the project, in the folder containing `pom.xml` use the terminal to run `mvn package`
* Run the project using `java -jar target/rate-product-service-0.1.0.jar`
* try using it by calling : 
GET `http://localhost:8080/estimate?keyword=<your_keyword>`

The response should be a json string as follows : 

`{
“Keyword”:<your_key_word>,
“score”:54
}`


#### How does the score calculation works : 

This happens by searching on 2 levels :

first search by using the keyword itself, then the second level of search is for every result we got from the first search.
The second search makes us (relatively) know the number of products or keywords that didn't appear in the first search which means they have the same prefix but have less ranking. 
This is not very accurate as the search result is only 10, so there might be larger numbers of keywords that comes after the keyword.
The final score is simple the number of products that we are sure they have same prefix and less ranking (that we got from the second searches)

In case the keyword is too generic (example : bla -> gets all products starting with black) then the score here will be the the size of the tree (which means the total number of results we will get from the second search).

In case the keyword result in an empty list we consider the score 0.