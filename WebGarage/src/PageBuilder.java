
public class PageBuilder {
	//private StringBuilder head;
	private StringBuilder body;
	private static final String ATTR_TEMPLATE = " %s='%s'";
	private static final String TAG_TEMPLATE = "<%s%s>%s</%s>";
	private static final String PAGE_TEMPLATE = 
			"<html>\n" +
			"\t<head>\n" +
			//"%s" +
			"\t</head>\n" +
			"\t<body>\n" +
			"%s" +
			"\t</body>\n" +
			"</html>";
	
	public PageBuilder() {
		//head = new StringBuilder();
		body = new StringBuilder();
	}
	
	public void addTag(String element, String content, boolean hasClosingTag, String...attributes) {
		body.append(createTag(element, content, hasClosingTag, attributes));
	}
	
	public String createTag(String element, String content, boolean hasClosingTag, String...attributes) {
		if(attributes.length % 2 != 0) throw new IllegalArgumentException();
		StringBuilder tag = new StringBuilder();
		StringBuilder attrs = new StringBuilder();
		for(int i = 0; i < attributes.length; i += 2) {
			attrs.append(String.format(ATTR_TEMPLATE, attributes[i], attributes[i+1]));
		}
		
		tag.append(
			String.format(
				TAG_TEMPLATE, 
				element,
				attrs.toString(),
				content,
				element,
				"\n"
			)
		);
		
		return tag.toString();
	}
	
	public String toString() {
		String page = String.format(PAGE_TEMPLATE, body.toString());
		return page;
	}
}
