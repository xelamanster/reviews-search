# Reviews-search

The project is uses Akka and EllasticSearch

At the current state must provide setting of the ES server with reviews data. The way to import data to the ElasticSearch is use of Logstash.

For correct search results. All fields should be not_analyzed except of text, for full text search of frequent words.

## Questions
Duplicates: 
Can be eliminated by producing Id for documents in es. If 	there are entries with different Id but same other 	content, then need to manualy set search queries  to find 	and remove duplicates. 

### Memmory: 
Project can be prepared to deployment as a docker image 	that contains application and es instance (another way is 	use embedded es instance, but in not more supported by 	elastic). For docker container can specified memory 	consumption boundaries. 

### Monitoring: 
Can be achieved by using standart scripts to show memory used by docker containers, and using tools like visual vm for connecting to the ES or application's vm.

### Distribution:
Akka and ES well scales horizontally. For that should add additional configurations for sharding. 


## NOT finished:
### Application build:
 - Docker image that contains app and ES instance;

### Aggregation searches:
 - Need to add sorting of the result;

### Translation:
 - Iteration through all reviews;
 - Choosing of language (should detect language? Can also           be achieved using ES);
 - Process translation in parallel accordingly to 	
limitations;

(created actors pool but dont went further. Idea is to create queue of translation bundles. Check queue size before each request and if it less than some treshold - make new scroll search. Also need to store and check current count of the translations that is in progress.

 Schedule updating of the pool actors. If there are available items in queue and not used all parallel capabilities of the translation api send items to the actors.) Or when worker actor send translation review - send it back new text to process;

### Cost efficient:
 Google translate api provide ability to choose few text parameters, so solution is in using multiply text paramenter in request. 
 If use description of the api from the task, where only one text argument available solutions is:
	- Count dots(need to test with other punctuation marks), concatenate few texts and after translation split them by counting marks. If review dont end with dot put it in the end of the query. If there are no reviews that ends with dot - put additional dots in reviews that will be in the middle of the query (Also can remove them after translation).
 
### Using arguments: 
 - Add translate parameter, path to the file with reviews;
 - Code refactoring;
 - Move search invocations from MasterActor;
 - Move uses of the storage calls to one place;
 - Make translation api more abstract;
 - Overall code review;