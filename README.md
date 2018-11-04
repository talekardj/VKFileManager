# VKFileManager
Its an Application to do required string replacements in locale files.
Currently written using Java8 libraries



Run as ::: 
- create an executable JAR using 'mvn package' goal
- copy vkFileManager.properties at same location where above JAR (vkFileManager-xxx-jar-with-dependencies.jar) is kept
- modify the vkFileManager.properties accordingly
- run the JAR using 'java -jar vkFileManager-xxx-jar-with-dependencies.jar'
- you will see the execution flow on console as well as in log file created in '/logs' directory (created in same directory as of JAR)



sample execution directory structure ::: 
VK
|___vkFileManager-0.0.1-SNAPSHOT-jar-with-dependencies.jar
|___vkFileManager.properties
|___inputs
			|___de-DE
					|___hybrid-cloud-computing#solutions[7997].xml
			|___fr-FR
					|___hybrid-cloud-computing#solutions[7997].html
|___logs
		|___vkFileManager_20181103_172048211.log
		|___vkFileManager_20181104_094011882.log



vkFileManager.properties ::: 
- vk.root.dir : 
	specify the root directory from which to start iterating it and processing the files
	e.g. D:/Dev/VK/inputs/
	
- vk.<folder name>.replacements=<comma separated replacement texts>
	specify strings (comma separated) to be replaced in files in this locale folder
	e.g. vk.de-DE.replacements=emc.com,www.emc.com,www.emc.com,dell.com,www.dell.com
		here, we tell program that, 
		all files in de-DE folder can have followingn replacable strings : 'emc.com,www.emc.com,www.emc.com,dell.com,www.dell.com'
		on finding any of these, replace it asmentioned in below property
		
- vk.<folder name>.replacement<text to be replaced>=<replacement text>
	specify string (that was mentioned just above line) and its replacement
	e.g. vk.de-DE.replacement.emc.com=germany.emc.com
		here, in all files in de-DE folder 'emc.com' will be replaced with 'germany.emc.com'



ReplacementStrategy ::: 
- REPLACE_TARGETTAGCONTENT : if file contains any occurance of tags '<source></source><target></target>'
	replace only in content between '<target><target>'
	e.g. 
		input : 
			<source>{"cta.title":"","cta":"{\"ctaParent\":false,\"iconPath\":\"\",\"url\":\"http://www.emc.com/cloud/virtustream-enterprise-cloud\",\"text\":\"LEARN MORE\",\"type\":\"btn quaternary\",\"isExternal\":true,\"isHidden\":false,\"enablementLink\":\"\",\"ctaArrow\":\"\"}"}</source>
			<target>{"cta.title":"","cta":"{\"ctaParent\":false,\"iconPath\":\"\",\"url\":\"http://www.emc.com/cloud/virtustream-enterprise-cloud\",\"text\":\"LEARN MORE\",\"type\":\"btn quaternary\",\"isExternal\":true,\"isHidden\":false,\"enablementLink\":\"\",\"ctaArrow\":\"\"}"}</target>
		output : 
			<source>{"cta.title":"","cta":"{\"ctaParent\":false,\"iconPath\":\"\",\"url\":\"http://www.emc.com/cloud/virtustream-enterprise-cloud\",\"text\":\"LEARN MORE\",\"type\":\"btn quaternary\",\"isExternal\":true,\"isHidden\":false,\"enablementLink\":\"\",\"ctaArrow\":\"\"}"}</source>
			<target>{"cta.title":"","cta":"{\"ctaParent\":false,\"iconPath\":\"\",\"url\":\"http://www.germany.emc.com/cloud/virtustream-enterprise-cloud\",\"text\":\"LEARN MORE\",\"type\":\"btn quaternary\",\"isExternal\":true,\"isHidden\":false,\"enablementLink\":\"\",\"ctaArrow\":\"\"}"}</target>

- else REPLACE_ALLOCCURRENCES : 
	replace all found occurances in file
	e.g. 
		input : 
			<tr>
				<a>{"cta.title":"","cta":"{\"ctaParent\":false,\"iconPath\":\"\",\"url\":\"http://www.emc.com/cloud/virtustream-enterprise-cloud\",\"text\":\"LEARN MORE\",\"type\":\"btn quaternary\",\"isExternal\":true,\"isHidden\":false,\"enablementLink\":\"\",\"ctaArrow\":\"\"}"}</a>
				<a>{"cta.title":"","cta":"{\"ctaParent\":false,\"iconPath\":\"\",\"url\":\"http://www.emc.com/en-US/cloud/virtustream-enterprise-cloud\",\"text\":\"LEARN MORE\",\"type\":\"btn quaternary\",\"isExternal\":true,\"isHidden\":false,\"enablementLink\":\"\",\"ctaArrow\":\"\"}"}</a>
			</tr>
		output : 
			<tr>
				<a>{"cta.title":"","cta":"{\"ctaParent\":false,\"iconPath\":\"\",\"url\":\"http://www.germany.emc.com/cloud/virtustream-enterprise-cloud\",\"text\":\"LEARN MORE\",\"type\":\"btn quaternary\",\"isExternal\":true,\"isHidden\":false,\"enablementLink\":\"\",\"ctaArrow\":\"\"}"}</a>
				<a>{"cta.title":"","cta":"{\"ctaParent\":false,\"iconPath\":\"\",\"url\":\"http://www.germany.emc.com/de-de/cloud/virtustream-enterprise-cloud\",\"text\":\"LEARN MORE\",\"type\":\"btn quaternary\",\"isExternal\":true,\"isHidden\":false,\"enablementLink\":\"\",\"ctaArrow\":\"\"}"}</a>
			</tr>
