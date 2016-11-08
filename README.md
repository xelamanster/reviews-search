# Reviews-search

The project is uses Akka and EllasticSearch. Reviews data should be loaded to the ES.
At the current state need to import data to the ES using LogStash. Also need to manually set ES server location in code (if not default).
Provided logstash-test.conf, for correct working should change ES, reviews and sinceDb locations.

For correct search results. All fields should be not_analyzed except of text, for full text search of frequent words.

## Questions
### Duplicates:
Can be eliminated by producing Id for documents in es. If 	there are entries with different Id but same other 	content, then need to manual set search queries  to find 	and remove duplicates.

### Memory:
Project can be prepared to deployment as a docker image 	that contains application and es instance (another way is 	use embedded es instance, but in not more supported by 	elastic). For docker container can specified memory 	consumption boundaries.

### Monitoring:
Can be achieved by using standard scripts to show memory used by docker containers, and using tools like visual vm for connecting to the ES or application's vm.

### Distribution:
Akka and ES can scale well horizontally. For that should add additional sharding configurations.


## NOT finished:
### ES configuration:
 - Implementation of the importing without using of Logstash
 - Need configure index manually to provide analyzed text field;

### Application build:
 - Docker image that contains app and ES instance;

### Aggregation searches:
 - Need to add sorting of the result;

### Translation:
 - Iteration through all reviews;
 - Choosing of language (should detect language? Can also be achieved using ES);
 - Process translation in parallel accordingly to
limitations;

Idea of parallel translation is:

 - Create queue of translation texts;
 - Check queue size before each request and if it less than set threshold - make new scroll search;
 - Also need to store and check current count of the translations that is in progress;

 - Schedule updating of the pool actors( If there are available items in queue and not used all parallel capabilities of the translation api send items to the actors)
Or at the other hand, send new text to process when actor send previous translated review;

### Cost efficient:
Google translate api provide ability to choose few text parameters, so solution is in using multiply text parameter in request.

If use description of the api from the task, where only one text argument available solutions is:

Count dots(need to test with other punctuation marks), concatenate few texts and after translation split them by counting marks. If review don't end with dot put it in the end of the query. If there are no reviews that ends with dot - put additional dots in reviews that will be in the middle of the query (Also can remove them after translation).

### Code maintainability and readability:
 - Documentation;
 - Move search invocations from MasterActor;
 - Move uses of the storage calls to one place;
 - Make translation api more abstract;
 - Overall code review;

### Using arguments: 
 - Add translate parameter, path to the file with reviews;