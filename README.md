json-wikipedia ![json-wikipedia](https://dl.dropboxusercontent.com/u/4663256/tmp/json-wikipedia.png) 
==============

 Json Wikipedia contains code to convert the Wikipedia XML dump into a [JSON][json] dump.
 
 - Please be aware that this tool does not work with the `multistream` dump.

 What's different about this fork:
 
 - Uses Apache Spark to speedup the transformation to json. Original Json-wikipedia runs on a single thread.
 - Fixes some issues with JWPL, which means less noisy extractions
 - Chunks the article's pages into paragraphs and returns a list of links with correct spans
 - Extract Links from article's paragraphs with matching spans


## Convert the Wikipedia XML to JSON

### The Docker way

Enjoy a dockerized image:

   `docker run -v <LOCALPATH>:/mnt -i -t dav009/jsonpedia -input <PATHTOWIKI> -output <OUTPUTPATH> -lang <LANG>`

 For example if my `english_wikipedia.dump` lives in : `/david/data/english_wikipedia.dump` I could run it as:

   `docker run -v /david/data:/mnt -i -t dav009/jsonpedia -input /mnt/english_wikipedia.dump -output /mnt/english_wikipedia.json -lang en`

Note that the output path corresponds to a path within the docker container. In the given example the output path is part of the mounted volume, so it will be available at the host machine.


### Doing it yourself

1. Compile the project by doing: `mvn assembly:assembly` the command will produce a JAR file containing all the dependencies the target folder.
2. Download Spark 1.3.1: http://www.apache.org/dyn/closer.cgi/spark/spark-1.3.1/spark-1.3.1.tgz
3. Download Wikipedia Dump ( https://dumps.wikimedia.org/backup-index.html )
4. Uncompress the Wikipedia Dump
5. do:

    SPARKFOLDER/bin/spark-submit --driver-memory 10G --class it.cnr.isti.hpc.wikipedia.cli.MediawikiToJsonCLI json-wikipedia-1.0.0-jar-with-dependencies.jar -input <PATHTODBPEDIADUMP> -output <PATHTONEWJSONPEDIA> -lang <LANG>

this produces in `<PATHTONEWJSONPEDIA>` the JSON version of the dump

### How does Jsonpedia look like?

 ([here you can find an example](https://dl.dropboxusercontent.com/u/4663256/tmp/json-wikipedia-sample.json)). Each line of the file contains an article
of dump encoded in JSON. Each JSON line can be deserialized in an [Article](http://sassicaia.isti.cnr.it/javadocs/json-wikipedia/it/cnr/isti/hpc/wikipedia/article/Article.html) object,which represents an_enriched_ version of the wikitext page. The Article object contains:


  * the title (e.g., Leonardo Da Vinci);
  * the wikititle (used in Wikipedia as key, e.g., Leonardo\_Da\_Vinci);
  * the namespace and the integer namespace in the dump;
  * the timestamp of the article;
  * the type, if it is a standard article, a redirection, a category and so on;
  * if it is not in English the title of the correspondent English Article;
  * a list of  tables that appear in the article ;
  * a list of lists that  that appear in the article ;
  * a list  of internal links that appear in the article;
  * a list of external links that appear in the article;
  * if the article  is a redirect, the pointed article;
  * a list of section titles in the article;
  * the text of the article, divided in paragraphs (PLAIN, no wikitext);
  * the categories and the templates of the articles;
  * the list of attributes found in the templates;
  * a list of terms highlighted in the article;
  * if present, the infobox. 
  
#### Usage

Once you have created (or downloaded) the JSON dump (say `wikipedia.json`), you can iterate over the articles of the collection 
easily using this snippet: 

    RecordReader<Article> reader = new RecordReader<Article>(
      "wikipedia.json",new JsonRecordParser<Article>(Article.class)
    ).filter(TypeFilter.STD_FILTER);

    for (Article a : reader) {
    // do what you want with your articles  
    }
 
You can also add some filters in order to iterate only on certain articles (in the example 
we used only the standard type filter, which excludes meta pages e.g., Portal: or User: pages.).

The [RecordReader](http://sassicaia.isti.cnr.it/javadocs/hpc-utils/it/cnr/isti/hpc/io/reader/RecordReader.html) and 
[JsonRecordParser](http://sassicaia.isti.cnr.it/javadocs/hpc-utils/it/cnr/isti/hpc/io/reader/JsonRecordParser.html) are part
of the [hpc-utils](http://sassicaia.isti.cnr.it/javadocs/hpc-utils) package.

In order to use these classes, you will have to install `json-wikipedia` in your maven repository:

    mvn install

and import the project in your new maven project adding the dependency: 

	<dependency>
		<groupId>it.cnr.isti.hpc</groupId>
		<artifactId>json-wikipedia</artifactId>
		<version>1.0.0</version>
	</dependency>
  
#### Schema ####

```
 |-- categories: array (nullable = true)
 |    |-- element: struct (containsNull = false)
 |    |    |-- anchor: string (nullable = true)
 |    |    |-- id: string (nullable = true)
 |-- externalLinks: array (nullable = true)
 |    |-- element: struct (containsNull = false)
 |    |    |-- anchor: string (nullable = true)
 |    |    |-- id: string (nullable = true)
 |-- highlights: array (nullable = true)
 |    |-- element: string (containsNull = false)
 |-- infobox: struct (nullable = true)
 |    |-- anchor: array (nullable = true)
 |    |    |-- element: string (containsNull = false)
 |    |-- name: string (nullable = true)
 |-- integerNamespace: integer (nullable = true)
 |-- lang: string (nullable = true)
 |-- links: array (nullable = true)
 |    |-- element: struct (containsNull = false)
 |    |    |-- anchor: string (nullable = true)
 |    |    |-- id: string (nullable = true)
 |-- lists: array (nullable = true)
 |    |-- element: array (containsNull = false)
 |    |    |-- element: string (containsNull = false)
 |-- namespace: string (nullable = true)
 |-- paragraphs: array (nullable = true)
 |    |-- element: string (containsNull = false)
 |-- redirect: string (nullable = true)
 |-- sections: array (nullable = true)
 |    |-- element: string (containsNull = false)
 |-- tables: array (nullable = true)
 |    |-- element: struct (containsNull = false)
 |    |    |-- name: string (nullable = true)
 |    |    |-- numCols: integer (nullable = true)
 |    |    |-- numRows: integer (nullable = true)
 |    |    |-- table: array (nullable = true)
 |    |    |    |-- element: array (containsNull = false)
 |    |    |    |    |-- element: string (containsNull = false)
 |-- templates: array (nullable = true)
 |    |-- element: struct (containsNull = false)
 |    |    |-- anchor: array (nullable = true)
 |    |    |    |-- element: string (containsNull = false)
 |    |    |-- name: string (nullable = true)
 |-- templatesSchema: array (nullable = true)
 |    |-- element: string (containsNull = false)
 |-- timestamp: string (nullable = true)
 |-- title: string (nullable = true)
 |-- type: string (nullable = true)
 |-- wid: integer (nullable = true)
 |-- wikiTitle: string (nullable = true)
```

#### Useful Links ####

  * [**Dexter**](http://dexter.isti.cnr.it) Dexter is an entity annotator, json-wikipedia is used in order to generate the model for performing the annotations. 
  * [**json-wikipedia Javadoc**](http://sassicaia.isti.cnr.it/javadocs/json-wikipedia) The json-wikipedia javadoc.
  * [**hpc-utils Javadoc**](http://sassicaia.isti.cnr.it/javadocs/hpc-utils) The hpc-utils Javadoc.


[json]: http://www.json.org/fatfree.html "JSON: The Fat-Free Alternative to XML"


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/diegoceccarelli/json-wikipedia/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

