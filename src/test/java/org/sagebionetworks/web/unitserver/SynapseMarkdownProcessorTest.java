package org.sagebionetworks.web.unitserver;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.web.server.SynapseMarkdownProcessor;

public class SynapseMarkdownProcessorTest {

	SynapseMarkdownProcessor processor = SynapseMarkdownProcessor.getInstance();
	
	@Before
	public void setup() {
		
	}
	
	@Test
	public void testMarkdown2HtmlEscapeControlCharacters() throws IOException{
		String testString = "& ==> &amp;\" ==> &quot;> ==> &gt;< ==> &lt;' =";
		
		String actualResult = processor.markdown2Html(testString, false);
		assertTrue(actualResult.contains("&amp; ==&gt; &amp;&quot; ==&gt; &quot;&gt; ==&gt; &gt; &lt; ==&gt; &lt;' ="));
	}
	
	@Test
	public void testWhitespacePreservation() throws IOException{
		String codeBlock = " spaces  and\nnewline    -  test\n  preservation in preformatted  code blocks";
		String testString = "```\n"+codeBlock+"\n```";
		String actualResult = processor.markdown2Html(testString, false);
		//it should contain the code block, exactly as written
		assertTrue(actualResult.contains(codeBlock));
	}
	
	@Test
	public void testRemoveAllHTML() throws IOException{
		String testString = "<table><tr><td>this is a test</td><td>column 2</td></tr></table><iframe width=\"420\" height=\"315\" src=\"http://www.youtube.com/embed/AOjaQ7Vl7SM\" frameborder=\"0\" allowfullscreen></iframe><embed>";
		String actualResult = processor.markdown2Html(testString, false);
		assertTrue(!actualResult.contains("<table>"));
		assertTrue(!actualResult.contains("<iframe>"));
		assertTrue(!actualResult.contains("<embed>"));
	}
	
	@Test
	public void testRAssign() throws IOException{
		//testing R assignment operator (html stripping should not alter)
		String testString = "DemoClinicalOnlyModel <- setRefClass(Class  = \"CINModel\",...";
		String actualResult = processor.markdown2Html(testString, false);
		//there should be no space between the less than and the dash:
		assertTrue(actualResult.contains("&lt;-"));
	}
	
	@Test
	public void testTableSupport() throws IOException{
		String testString = 
				"|             |          Grouping           ||\nFirst Header  | Second Header | Third Header |\n ------------ | :-----------: | -----------: |\nContent       |          *Long Cell*        ||\nContent       |   **Cell**    |         Cell |\n";
		
		String actualResult = processor.markdown2Html(testString, false);
		assertTrue(actualResult.contains("<table"));
		assertTrue(actualResult.contains("<tr>"));
		assertTrue(actualResult.contains("<td>"));
	}
	
	@Test
	public void testListAndHeaderInBlockquote() throws IOException{
		//complicated integration test of all parsers
		String testString = "> * Item 1\n> * Item 2\n>   1. #### SubItem 2a\n>   2. SubItem 2b\n> ``` r\n> Then a code block!\n> ```";
		String actualResult = processor.markdown2Html(testString, false);
		String expectedResult = "<blockquote>\n   <ul>\n    <li>Item 1</li>\n    <ul>\n     <li>Item 2</li>\n     <ol>\n      <li><h4 id=\"synapseheading0\" level=\"h4\" toc-style=\"toc-indent0\">SubItem 2a</h4></li>\n      <li>SubItem 2b</li>\n     </ol>\n    </ul>\n   </ul>\n   <pre><code class=\"r\">Then a code block! </code></pre>\n   <br /> \n  </blockquote>";
		assertTrue(actualResult.contains(expectedResult));
	}
	
	@Test
	public void testTableThenHR() throws IOException{
		//complicated integration test of all parsers
		String testString = "Tau | MAPT | MCF7,BT20\nVASP | VASP | MCF7,BT20\nXIAP | XIAP | MCF7,BT20\n--------------------------------\n## Additional Data Details";
		String actualResult = processor.markdown2Html(testString, false);
		assertTrue(actualResult.contains("<hr"));
		assertFalse(actualResult.contains("<del"));
	}
	
}