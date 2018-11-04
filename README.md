# VKFileManager
Its an Application to do required string replacements in locale files.
Currently written using Java8 libraries
<br/>
<br/>
<br/>
<b>Run as ::: </b>
- create an executable JAR using 'mvn package' goal
- copy 'vkFileManager.properties' at same location where above JAR (vkFileManager-xxx-jar-with-dependencies.jar) is kept
- modify the 'vkFileManager.properties' accordingly
- run the JAR using 'java -jar vkFileManager-xxx-jar-with-dependencies.jar'
- you will see the execution flow on console as well as in log file created in '/logs' directory (created in same directory as of JAR)
<br/>
<br/>
<br/>
<b>sample execution directory structure ::: </b><br/>
<table>
	<tr>
		<td colspan="1" align="left">VK</td>
		<td colspan="3" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
	<tr>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="3" align="left">vkFileManager-0.0.1-SNAPSHOT-jar-with-dependencies.jar</td>
	</tr>
	<tr>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="3" align="left">vkFileManager.properties</td>
	</tr>
	<tr>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="3" align="left">inputs</td>
	</tr>
	<tr>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="2" align="left">de-DE</td>
	</tr>
	<tr>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="1" align="left">hybrid-cloud-computing#solutions[7997].xml</td>
	</tr>
	<tr>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="2" align="left">fr-FR</td>
	</tr>
	<tr>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="1" align="left">hybrid-cloud-computing#solutions[7997].html</td>
	</tr>
	<tr>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="3" align="left">logs</td>
	</tr>
	<tr>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="2" align="left">vkFileManager_20181103_172048211.log</td>
	</tr>
	<tr>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="1" align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td colspan="2" align="left">vkFileManager_20181104_094011882.log</td>
	</tr>
	</table>
<br/>
<br/>
<br/>
<b>vkFileManager.properties ::: </b>

- vk.root.dir : <br/>
	specify the root directory from which to start iterating it and processing the files<br/>
	e.g. D:/Dev/VK/inputs/<br/>
	
- vk.<folder name>.replacements=<comma separated replacement texts><br/>
	specify strings (comma separated) to be replaced in files in this locale folder<br/>
	e.g. vk.de-DE.replacements=emc.com,www.emc.com,www.emc.com,dell.com,www.dell.com<br/>
		here, we tell program that, <br/>
		all files in 'de-DE' folder can have following replacable strings : 'emc.com,www.emc.com,www.emc.com,dell.com,www.dell.com'<br/>
		on finding any of these, replace it as mentioned in below property<br/>
		
- vk.<folder name>.replacement<text to be replaced>=<replacement text><br/>
	specify string (that was mentioned just above line) and its replacement<br/>
	e.g. vk.de-DE.replacement.emc.com=germany.emc.com<br/>
		here, in all files in 'de-DE folder', 'emc.com' will be replaced with 'germany.emc.com'<br/>
<br/>
<br/>
<br/>
<b>Replacement strategies ::: </b>
	
- REPLACE_TARGETTAGCONTENT : <br/>
	if file contains any occurance of tags '<source></source><target></target>'<br/>
	do replacements only in content between '<target><target>' tags<br/>
	e.g. <br/>
		input : <br/><br/>
			<source>{"cta.title":"","cta":"{\"ctaParent\":false,\"iconPath\":\"\",\"url\":\"http://www.emc.com/cloud/virtustream-enterprise-cloud\",\"text\":\"LEARN MORE\",\"type\":\"btn quaternary\",\"isExternal\":true,\"isHidden\":false,\"enablementLink\":\"\",\"ctaArrow\":\"\"}"}</source><br/>
			<target>{"cta.title":"","cta":"{\"ctaParent\":false,\"iconPath\":\"\",\"url\":\"http://www.emc.com/cloud/virtustream-enterprise-cloud\",\"text\":\"LEARN MORE\",\"type\":\"btn quaternary\",\"isExternal\":true,\"isHidden\":false,\"enablementLink\":\"\",\"ctaArrow\":\"\"}"}</target><br/><br/>
		output : <br/><br/>
			<source>{"cta.title":"","cta":"{\"ctaParent\":false,\"iconPath\":\"\",\"url\":\"http://www.emc.com/cloud/virtustream-enterprise-cloud\",\"text\":\"LEARN MORE\",\"type\":\"btn quaternary\",\"isExternal\":true,\"isHidden\":false,\"enablementLink\":\"\",\"ctaArrow\":\"\"}"}</source><br/>
			<target>{"cta.title":"","cta":"{\"ctaParent\":false,\"iconPath\":\"\",\"url\":\"http://www.germany.emc.com/cloud/virtustream-enterprise-cloud\",\"text\":\"LEARN MORE\",\"type\":\"btn quaternary\",\"isExternal\":true,\"isHidden\":false,\"enablementLink\":\"\",\"ctaArrow\":\"\"}"}</target><br/><br/>

- else REPLACE_ALLOCCURRENCES : <br/>
	replace all found occurances in file<br/>
	e.g. <br/>
		input : <br/>
			<tr><br/>
				<a>{"cta.title":"","cta":"{\"ctaParent\":false,\"iconPath\":\"\",\"url\":\"http://www.emc.com/cloud/virtustream-enterprise-cloud\",\"text\":\"LEARN MORE\",\"type\":\"btn quaternary\",\"isExternal\":true,\"isHidden\":false,\"enablementLink\":\"\",\"ctaArrow\":\"\"}"}</a>
				<a>{"cta.title":"","cta":"{\"ctaParent\":false,\"iconPath\":\"\",\"url\":\"http://www.emc.com/en-US/cloud/virtustream-enterprise-cloud\",\"text\":\"LEARN MORE\",\"type\":\"btn quaternary\",\"isExternal\":true,\"isHidden\":false,\"enablementLink\":\"\",\"ctaArrow\":\"\"}"}</a><br/>
			</tr><br/>
		output : <br/>
			<tr><br/>
				<a>{"cta.title":"","cta":"{\"ctaParent\":false,\"iconPath\":\"\",\"url\":\"http://www.germany.emc.com/cloud/virtustream-enterprise-cloud\",\"text\":\"LEARN MORE\",\"type\":\"btn quaternary\",\"isExternal\":true,\"isHidden\":false,\"enablementLink\":\"\",\"ctaArrow\":\"\"}"}</a><br/>
				<a>{"cta.title":"","cta":"{\"ctaParent\":false,\"iconPath\":\"\",\"url\":\"http://www.germany.emc.com/de-de/cloud/virtustream-enterprise-cloud\",\"text\":\"LEARN MORE\",\"type\":\"btn quaternary\",\"isExternal\":true,\"isHidden\":false,\"enablementLink\":\"\",\"ctaArrow\":\"\"}"}</a><br/>
			</tr><br/>
