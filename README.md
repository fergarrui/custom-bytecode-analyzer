# custom-bytecode-analyzer

Java bytecode analyzer customizable via JSON rules. It is a command-line tool that receives a path containing one or more [Jar](https://en.wikipedia.org/wiki/JAR_(file_format)) or [War](https://en.wikipedia.org/wiki/WAR_(file_format)) files, analyzes them using the provided rules and generates HTML reports with the results.

[![Build Status](https://travis-ci.org/fergarrui/custom-bytecode-analyzer.svg?branch=master)](https://travis-ci.org/fergarrui/custom-bytecode-analyzer)

## Usage

```
usage: java -jar cba-cli.jar [OPTIONS] -a DIRECTORY_TO_ANALYZE
 -a,--analyze <pathToAnalyze>    Path of the directory to run the
                                 analysis.
-c,--checks <checks...>          Space separated list of custom checks
								 that are going to be run in the analysis.
 -f,--custom-file <customFile>   Specify a file in JSON format to run
                                 custom rules. Read more in
                                 https://github.com/fergarrui/custom-bytecode-analyzer.
 -h,--help                       Print this message.
 -i,--items-report <maxItems>    Max number of items per report. If the
                                 number of issues found exceeds this
                                 value, the report will be split into
                                 different files. Useful if expecting too
                                 many issues in the report. Default: 50.
 -o,--output <outputDir>         Directory to save the report. Warning -
                                 if there are already saved reports in
                                 this directory they will be overwritten.
                                 Default is "report".
 -v,--verbose-debug              Increase verbosity to debug mode.
 -vv,--verbose-trace             Increase verbosity to trace mode - makes it slower, use it only when you need.
```

## Custom JSON rules

Rules file can be specified using ```-f,--custom-file``` argument . The file is in JSON format and has the following structure:

* rules : array(rule)
    * name : string
    * fields : array(field)
        * visibility : (public|protected|private)
        * type : string
        * valueRegex : string (java regular expression) - only supported if the field is ```final```
        * nameRegex : string (java regular expression)
        * report : boolean (default: true)
    * interfaces : array(string)
    * superClass : string
    * annotations : array(annotation)
        * type : string
        * report : boolean (default: true)
    * methods :  array(method)
        * name : string
        * visibility : (public|protected|private)
        * parameters : array(parameter)
            * type : string
            * report : boolean (default: true)
            * annotations : array(annotation)
                * type : string
                * report : boolean (default: true)
        * variables : array(variable)
            * type : string
            * nameRegex : string (java regular expression)
            * annotations : array(annotation)
                * type : string
                * report : boolean (default: true)
            * report (default: true)
        * annotations : array(annotation)
            * type : string
            * report : boolean (default: true)
        * report : boolean (default: true)
    * invocations : array(invocation)
        * owner : string
        * method : method
            * name : string
            * visibility : (public|protected|private)
        * from : method
            * name : string
            * visibility : (public|protected|private)
        * notFrom : method
            * name : string
            * visibility : (public|protected|private)
        * report : boolean (default:true)

You can also check ```net.nandgr.cba.custom.model.Rules.java``` to see the structure in Java code.

### Examples

There are already several rules under the directory [examples](https://github.com/fergarrui/custom-bytecode-analyzer/tree/master/examples) .
Anyway, below are listed examples for every rule.

#### Find custom deserialization
If we need to find classes with custom deserialization, we can do it quite easily. A class defines custom deserialization by implementing ```private void readObject(ObjectInputStream in)```. So we only need to find all classes where that method is defined. It would be enough just to define a rule as:

```json
{
	"rules": [{
		"name": "Custom deserialization",
		"methods": [{
			"name": "readObject",
			"visibility": "private",
			"parameters" : [{
         "type" : "java.io.ObjectInputStream"
      }]
		}]
	}]
}
```

It will report methods with ```private``` visibility, ```readObject``` as name and a parameter of type ```java.io.ObjectOutputStream```. Parameters are an array, if more than one is specified, all of them have to match to be reported. Since we only have one rule, a report named: custom-deserialization-0.html will be created.

#### Find custom serialization and deserialization

In this case, one rule with two methods have to be defined. The same one than in the previous example for deserialization, and a new one to match ```private void writeObject(ObjectOutputStream out)```. As shown in the JSON structure above, the property rules.rule.methods is an array of methods, so a rule like this can be written:

```json
{
  "rules": [{
    "name": "Custom serialization and deserialization",
    "methods": [{
      "name": "readObject",
      "visibility": "private",
      "parameters" : [{
       "type" : "java.io.ObjectInputStream"
      }]
    },{
      "name": "writeObject",
      "report": "false",
      "visibility": "private",
      "parameters" : [{
        "type" : "java.io.ObjectOutputStream"
      }]
    }]
  }]
}
```

The property ```report``` was set to false to avoid reporting twice for the same rule. We are using the second method just as a condition, but reporting only ```readObject``` methods should be enough for the purpose of this rule.

#### Find all method definitions
If a property is not defined, it will always match as true. For example, this rule would return all methods definitions:
```json
{
	"rules": [{
		"name": "Method definitions",
		"methods": [{
		}]
	}]
}
```

#### Find String.equals method invocations

Method invocations can also be found. The JSON in this case would be:
```json
{
	"rules": [{
		"name": "String equals",
		"invocations": [{
			"owner": "java.lang.String",
			"method": {
				"name": "equals"
			}
		}]
	}]
}
```
The property ```owner``` specifies the class containing the method.

#### Reflection method invoke

Another method invocation example a bit more useful than the previous one:
```json
{
	"rules": [{
		"name": "Method invocation by reflection",
		"invocations": [{
			"owner": "java.lang.reflect.Method",
			"method": {
				"name": "invoke"
			}
		}]
	}]
}
```

#### Find String instantiations

It is the same than any method invocation, but the name of the method in this case, should be ```<init>```.

```json
{
  "rules": [{
    "name" : "String instantiation",
    "invocations" : [{
        "owner" : "java.lang.String",
        "method" : {
          "name" : "<init>"
        }
    }]
  }]
}
```
This rule will find occurrences of:
```java
[...]
String s = new String("foo");
[...]
```

#### Deserialization usage
In this example, we want to find deserialization usages (not classes defining serialization behaviors like in the previous examples). Deserialization happens when ```ObjectInputStream.readObject()``` is invoked. for example in this code snippet:

```java
ObjectInputStream in = new ObjectInputStream(fileInputStream);
Object o = in.readObject();
```

So we need to find method invocations from ```ObjectInputStream``` named ```readObject```. But it will find a lot of false positives in a researching context, because when a class defines custom deserialization, they make an invocation to this method inside a ```private void readObject(ObjectInputStream in)``` method, and that would pollute the report too much. If we want to exclude those cases and keep only genuine deserialization, ```notFrom``` property can be used:

```json
{
	"rules": [{
		"name": "Deserialization usage",
		"invocations": [{
			"owner": "java.io.ObjectInputStream",
			"method": {
				"name": "readObject"
			},
			"notFrom": {
				"name": "readObject",
				"visibility": "private"
			}
		}]
	}]
}
```
This file will find ```java.io.ObjectInputStream.readObject()``` invocations if the invocation is not done inside ```private void readObject(ObjectInputStream in)``` method.

A class compiled with this code will not be reported:

```java
private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      Object o = in.readObject();
}
```
But this one will be reported:

```java
public Object deserializeObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      Object o = in.readObject();
      return o;
}
```

The property ```from``` can be set in invocations in exactly the same way than ```notFrom```, but the result will be the opposite: it will only match if the invocation is made from the defined method.

#### Java servlets

The property ```superClass```  can be used in this case. If we want to find all classes extending ```javax.servlet.http.HttpServlet```, a rule can be:

```json
{
  "rules": [{
    "name": "Java servlets",
    "superClass" : "javax.servlet.http.HttpServlet"
  }]
}

```

#### Interface implementations

A rule can be written to find classes implementing an array of interfaces. if more than one interface is defined in the rule, the class has to implement all of them to be reported. If we want to find classes implementing ```javax.net.ssl.X509TrustManager```, the rule would be:

```json
{
  "rules": [{
    "name": "X509TrustManager implementations",
    "interfaces" : ["javax.net.ssl.X509TrustManager"]
  }]
}
```

Please note that ```interfaces``` is an *array*, so make sure you add the strings between square brackets, e.g: ```["interface1", "interface2", ...]```.

#### Find Spring endpoints

Annotations are also supported. Multiple annotations properties can be defined in a rule (finding class annotations), in methods o variables (parameters or local variables). If all of them are found in the analyzed class, it will be reported.
For example, if we want to find Spring endpoints, we would search for classes or methods annotated with ```org.springframework.web.bind.annotation.RequestMapping```. So, the rule can be:

```json
{
  "rules": [{
    "name": "Spring endpoint - class annotation",
    "annotations" : [{
      "type" : "org.springframework.web.bind.annotation.RequestMapping"
    }]
  },
  {
     "name": "Spring endpoint - method annotation",
     "methods" : [{
        "annotations" : [{
          "type" : "org.springframework.web.bind.annotation.RequestMapping"
        }]
      }]
  }]
}
```

#### Find fields

The property ```rule.fields``` can be used to find class fields. If we want to find private String fields with password names, a rule like this one could be used:

```json
{
  "rules": [{
    "name" : "Password fields",
    "fields" : [
      {
        "visibility" : "private",
        "type" : "java.lang.String",
        "nameRegex" : "(password|pass|psswd|passwd)"
      }
    ]
  }]
}
```

#### Find variables

To find variables, ```rule.variables``` can be used. This property will report local variables and method arguments variables.
If we want to find all variables of type ```javax.servlet.http.Part```, a rule could be:

```json
{
  "rules": [{
    "name" : "Servlet upload file",
    "methods" : [{
      "variables" : [{
          "type" : "javax.servlet.http.Part"
      }]
    }]
  }]
}
```

#### Define multiple rules
Multiple rules can be defined in the same JSON file. They will be processed and reported separately and they will not affect each other. We can combine some of the previous examples rules:

```json
{
	"rules": [{
		"name": "Custom deserialization",
		"methods": [{
			"name": "readObject",
			"visibility": "private",
			"parameters": [{
        "type" : "java.io.ObjectInputStream"
      }]
		}]
	},{
		"name": "Method invocation by reflection",
		"invocations": [{
			"owner": "java.lang.reflect.Method",
			"method": {
				"name": "invoke"
			}
		}]
	}]
}
```

Here, we have two rules ("Custom deserialization" and "Method invocation by reflection"). They will be processed as if you do it in two separated executions. And a report per rule will be generated. If the rules have the same name, they will be reported in the same file.

## Custom Java rules

The project can be downloaded and built to add more complex custom rules in Java code that are not covered by the JSON format. There are already three examples under the package ```net.nandgr.cba.visitor.checks```. Those are ```CustomDeserializationCheck, DeserializationCheck and InvokeMethodCheck```. You can create your own rules by extending ```net.nandgr.cba.custom.visitor.base.CustomAbstractClassVisitor```.

## Reports

As mentioned above, the reports are created by default under ```report``` folder. Every rule will have a separate file unless they have the same name.
If the report is too big, you can split it using the ```-i,--items-report <maxItems>``` parameter, each of them will hold the argument specified or less (if it is the last one).
Every reported item, specifies the jar where it is found, the class name and the method name (if it is relevant). It also shows the decompiled version of the class to ease a quick visual check.
Example of how the items are shown for a rule to find ```java.io.File``` instantiations:

![Report example](images/report_image.png)

### Call graph

When searching for security bugs it is very useful to have a call graph. At the moment, a simple [DOT](https://en.wikipedia.org/wiki/DOT_(graph_description_language)) compatible file is created under the ```report``` directory.
The graph contains all the possible flows where the found issues can be invoked from. For example, if a rule to find deserialization is used,
a graph containing all possible paths leading to the method that calls the deserialization will be generated.

The file is ```call-graph.dot``` and it would look like this (this is an extremely simple example):

```
graph callGraph {
"demo.callgraph.Class1:method1" -- "demo.callgraph.Class2:method2"
"demo.callgraph.Class3:method3" -- "demo.callgraph.Class2:method2"
}
```

To display it in a visual way, ```DOT``` can be used (or any compatible software). For example, to convert the file to ```svg```:

```
dot -Tsvg call-graph.dot -o call-graph.svg
```

This is done automatically by default if DOT is found in the system PATH. If not, ```DOT``` can be installed in Debian based systems using ```sudo apt-get install graphviz```.

It will create a SVG file named ```call-graph.svg``` that can be converted into PNG or visualized using programs like ```inkscape``` or just ```firefox```.

A very simple example of the above file call-graph.dot, would be:

![Graph example](images/graph.png)

There are some limitations, like for example, if the searched item is in a ```java.lang.Runnable.run()``` or similar method, it will not find where the thread is executed from.
Also, the graph is cleaning cycles to avoid ```StackOverflowError```s, it is made in a bit of conservative way so the memory of the system is not drained during an analysis of a large directory.

More options will be added in future versions.

## Command line examples

#### Run an analysis using a JSON file
```
java -jar cba-cli-<version>.jar -a /path/with/jars -f /path/with/json/file/rules.json
```
#### Run an analysis using a Java custom rule
To use custom java rules, class names have to be specified as arguments of ```-c```.
```
java -jar cba-cli-<version>.jar -a /path/with/jars -c DeserializationCheck
```
Accepts a space separated list, so multiple custom rules can be defined (each of the rules will create a separate report):
```
java -jar cba-cli-<version>.jar -a /path/with/jars -c DeserializationCheck InvokeMethodCheck CustomDeserializationCheck YourCustomRule
```
#### Combine JSON and custom Java rules
```
java -jar cba-cli-<version>.jar -a /path/with/jars -f /path/with/json/file/rules.json -c YourCustomRule1 YourCustomRule2
```

#### Increase verbosity

To find errors, verbosity can be increased.
Debug level:
```
java -jar cba-cli-<version>.jar -a /path/with/jars -c YourCustomRule1 -v
```
Trace level:
```
java -jar cba-cli-<version>.jar -a /path/with/jars -c YourCustomRule1 -vv
```

## Analyze Android APKs

At the moment, the APK has to be converted to JAR first to be analyzed.

* Download dex2jar : https://github.com/pxb1988/dex2jar
* Convert DEX to JAR
    * ```d2j-dex2jar.sh -f -o app_to_analyze.jar app_to_analyze.apk```
* Run cba-cli.jar as usual passing as ```-a``` parameter the directory containing the converted jar file.

## Build and run the project

There is already an executable jar file under ```bin``` directory at: [https://github.com/fergarrui/custom-bytecode-analyzer/blob/master/bin/cba-cli-0.1-SNAPSHOT.jar](https://github.com/fergarrui/custom-bytecode-analyzer/blob/master/bin/cba-cli-0.1-SNAPSHOT.jar) . If you want to do modifications or add custom rules, the project can be built doing:

```
git clone https://github.com/fergarrui/custom-bytecode-analyzer.git
cd custom-bytecode-analyzer
mvn clean package
```
Two jars will be generated under ```target``` folder. ```cba-cli-<version>.jar``` contains all dependencies and is executable. Can be run using ```java -jar cba-cli-<version>.jar```
