package com.djt.vk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dhananjay <br/>
 * Checkout code from <a href="https://github.com/talekardj/VKFileManager">GitHub</a>
 */
public class VKFileManager
{
	private static final Date startTime = new Date();
	
	private static final Logger LOG;
	
	private static final String executionPath;
	
	private static Properties configProperties = new Properties();

	private static String activity = "";
	
	private static final List<String> succeeddedFiles = new ArrayList<>();
	private static final List<String> failedFiles = new ArrayList<>();

	private static final String VARIABLE_LOG_LOCATION = "logFileLocation";
	private static final String PROPERTY_FILENAME = "vkFileManager.properties";
	private static final String PROPERTY_KEY_ROOT_DIR = "vk.root.dir";
	private static final String PROPERTY_EXPR_REPLACEMENTS = "vk.{0}.replacements";
	private static final String PROPERTY_EXPR_REPLACEMENT = "vk.{0}.replacement.{1}";
	
	private static final String REPLACE_EXPR_IGNORECASE = "(?i)";
	private static final String REPLACE_EXPR_SOURCE_START = "<source[^/]*>";
	private static final String REPLACE_EXPR_SOURCE_END = "<\\/source>";
	private static final String REPLACE_EXPR_SOURCE = REPLACE_EXPR_SOURCE_START + ".+" + REPLACE_EXPR_SOURCE_END;
	private static final String REPLACE_EXPR_TARGET_START = "<target[^\\/]*>";
	private static final String REPLACE_EXPR_TARGET_END = "<\\/target>";
	private static final String REPLACE_EXPR_TARGET = REPLACE_EXPR_TARGET_START + ".+" + REPLACE_EXPR_TARGET_END;
	private static final String REPLACE_EXPR_SOURCE_TARGET = REPLACE_EXPR_SOURCE + "\\s*" + REPLACE_EXPR_TARGET;
	
	private enum ReplacementStrategy
	{
		REPLACE_ALLOCCURRENCES,
		REPLACE_TARGETTAGCONTENT
	}
	
	//--------------------------------------------------
	
	static
	{
		activity = "starting the app";
		
		executionPath = new File(VKFileManager.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getAbsolutePath();
		System.setProperty(VARIABLE_LOG_LOCATION, (executionPath + File.separator + "logs"));
		
		LOG = LoggerFactory.getLogger(VKFileManager.class);
		
		log();
		log("executionPath [" + executionPath + "]");
		log("logFileLocation [" + System.getProperty(VARIABLE_LOG_LOCATION) + "]");
	}
	
	private VKFileManager()
	{
		//DO NOTHING
	}
	
	//--------------------------------------------------
	
	public static void main(final String[] args)
	{
		log("started at [" + startTime + "]");
		
		//----------
		
		try
		{
			activity = "loading properties";
			log();
			loadProperties();
			log("properties loaded");
			
			activity = "get root directory path";
			String rootDirPath = getPropertyValue(PROPERTY_KEY_ROOT_DIR);
			log("rootDir [" + rootDirPath + "]");
			
			activity = "process root directory";
			processDirectory(rootDirPath);

			log("succefully processed [" + succeeddedFiles.size() + "] files [" + succeeddedFiles + "]");
			log("processing failed for [" + failedFiles.size() + "] files [" + failedFiles + "]");
		}
		catch (Exception ex)
		{
			logError(ex);
		}
		
		//----------
		
		final Date endTime = new Date();
		log("finished at [" + endTime + "]");
		log("time taken [" + ((endTime.getTime() - startTime.getTime()) / (60 * 1000)) + " mins]");
	}
	
	//--------------------------------------------------
	
	private static void loadProperties() throws IOException
	{
		String propertyFilePath = (executionPath + File.separator + PROPERTY_FILENAME);
		log("propertyFilePath [" + propertyFilePath + "]");
		
		final InputStream inputStream = new FileInputStream(propertyFilePath);
		configProperties.load(inputStream);
	}
	
	private static void processDirectory(final String rootDirPath) throws IOException
	{
		Files.walk(Paths.get(rootDirPath))
				.forEach(path -> {
									if(!Files.isDirectory(path))
									{
										processFile(path);
									}
								});
	}
	
	//--------------------------------------------------
	
	private static void log(final String info)
	{
		LOG.debug(info);
	}
	
	private static void log()
	{
		LOG.info("**[" + activity + "]**");
	}
	
	private static void logError(final Exception ex)
	{
		LOG.error("***[" + activity + "] : Exception caught : [" + ex.getMessage() + "]", ex);
	}
	
	//--------------------------------------------------
	
	private static String getPropertyValue(final String key) throws Exception
	{
		final String retVal = configProperties.getProperty(key);
		if(retVal != null)
		{
			return retVal;
		}
		else
		{
			throw new Exception("Property [" + key + "] not found in application.properties file");
		}
	}
	
	private static void processFile(final Path path)
	{
		try
		{
			activity = "processing [" + path.toString() + "]";
			log();
			
			//-----
			
			final String parentDirName = path.getParent().getFileName().toString();

			Map<String, String> replacementMap = getReplacementKeys(parentDirName);
			log("replacements for [" + parentDirName + "] : [" + replacementMap + "]");
			
			//-----
			
			final ReplacementStrategy replacementStrategy = getReplaceStrategyForFile(path);
			log("replacementStrategy [" + replacementStrategy + "]");
			
			//-----
			
			try (Stream<String> stream = Files.lines(path))
			{
				List<String> replaced = new ArrayList<>();
				stream.forEach(line -> {
											for(String replacementKey : replacementMap.keySet())
											{
												String replacementValue = replacementMap.get(replacementKey);
												line = replaceText(line, replacementStrategy, replacementKey, replacementValue);
											}
											replaced.add(line);
										});
				Files.write(path, replaced);//TODO : save with different fileName - compare line count
			}
			
			//-----
			
			succeeddedFiles.add(path.toString());
			log("processing [" + path.toString() + "] complete");
		}
		catch (Exception ex)
		{
			failedFiles.add(path.toString());
			logError(ex);
		}
	}
	
	//--------------------------------------------------
	
	private static Map<String, String> getReplacementKeys(final String parentDirName) throws Exception
	{
		final Map<String, String> replacementMap = new HashMap<>();
		
		final String replacementKeysStr = getPropertyValue(PROPERTY_EXPR_REPLACEMENTS.replace("{0}", parentDirName));
		final String [] replacementKeys = replacementKeysStr.split(",");
		
		for(String replacementKey : replacementKeys)
		{
			String replacementPropertyKey = PROPERTY_EXPR_REPLACEMENT.replace("{0}", parentDirName)
																		.replace("{1}", replacementKey);
			
			String replacementValue = getPropertyValue(replacementPropertyKey);
			
			replacementMap.put(replacementKey, replacementValue);
		}
		
		return replacementMap;
	}
	
	private static ReplacementStrategy getReplaceStrategyForFile(final Path filePath) throws IOException
	{
		ReplacementStrategy retVal = null;
		
		final String fileContent = new String(Files.readAllBytes(filePath));
		
		if(Pattern.compile(REPLACE_EXPR_IGNORECASE + REPLACE_EXPR_SOURCE_TARGET).matcher(fileContent).find())
		{
			retVal = ReplacementStrategy.REPLACE_TARGETTAGCONTENT;
		}
		else
		{
			retVal = ReplacementStrategy.REPLACE_ALLOCCURRENCES;
		}
		
		return retVal;
	}
	
	private static String replaceText(final String input, final ReplacementStrategy replacementStrategy, final String replacementKey, final String replacementValue)
	{
		String output = input;
		
		if(input != null && !input.trim().isEmpty())
		{
			if(replacementStrategy.equals(ReplacementStrategy.REPLACE_ALLOCCURRENCES))
			{
				output = input.replace(replacementKey, replacementValue);
			}
			else if(replacementStrategy.equals(ReplacementStrategy.REPLACE_TARGETTAGCONTENT))
			{
				Matcher targetMatcher = Pattern.compile(REPLACE_EXPR_IGNORECASE + REPLACE_EXPR_TARGET).matcher(input);	
				while(targetMatcher.find())
				{
					//asdasdasd<target>123</target>asdasdasd
					String tagString = targetMatcher.group();//<target>123</target>
					String tagContent = tagString.replaceAll(REPLACE_EXPR_IGNORECASE + REPLACE_EXPR_TARGET_START, "")//123</target>
													.replaceAll(REPLACE_EXPR_IGNORECASE + REPLACE_EXPR_TARGET_END, "");//123
					String updatedTagContent = tagContent.replace(replacementKey, replacementValue);//321
					String updatedTagString = tagString.replace(tagContent, updatedTagContent);//<target>321</target>
					
					output = input.replace(tagString, updatedTagString);//asdasdasd<target>321</target>asdasdasd
				}
			}
		}
		
		return output;
	}
}
