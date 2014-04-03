
public class PageBuilder {
	//private StringBuilder head;
	private Tag head = new Tag("head");
	private Tag body = new Tag("body");
	private Tag html = new Tag("html").addTags(head, body);
	
	public PageBuilder() {
		
	}
	
	public String toString() {
		return html.toString();
	}
	
	Tag getBody() {
		return body;
	}
	
	Tag createTag(String element) {
		return new Tag(element);
	}
	
	Tag createTag(String element, boolean hasClosingTag) {
		return new Tag(element, hasClosingTag);
	}
	
	static class Tag {
		private final String element;
		private String content;
		private Tag[] subTags = new Tag[0];
		private String[] attributes = new String[0];
		private boolean hasClosingTag = true;
		
		private Tag(String element) {
			this.element = element;
		}
		
		private Tag(String element, boolean hasClosingTag) {
			this.element = element;
			this.hasClosingTag = hasClosingTag;
		}
		
		Tag setSubTags(Tag...subTags) {
			this.subTags = subTags;
			this.hasClosingTag = true;
			return this;
		}
		
		Tag setAttributes(String...attributes) {
			if(attributes.length % 2 != 0) throw new IllegalArgumentException();
			this.attributes = attributes;
			return this;
		}
		
		Tag setContent(String content) {
			this.content = content;
			return this;
		}
		
		Tag addTags(Tag... tags) {
			Tag[] newTags = new Tag[subTags.length + tags.length];
			int i = 0;
			for(Tag tag : subTags) {
				newTags[i++] = tag;
			}
			for(Tag tag : tags) {
				newTags[i++] = tag;
			}
			this.subTags = newTags;
			return this;
		}
		
		public String toString() {
			return this.toTreeString(0);
		}
		
		private String toTreeString(int depth) {
			StringBuilder out = new StringBuilder();
			String depthTab = (depth == 0 ? "" : String.format(String.format("%%0%dd", depth), 0).replace("0","\t"));
			out.append(String.format("%s<%s", depthTab, element));
			for(int i = 0; i < attributes.length; i += 2) {
				out.append(String.format(" %s='%s'", attributes[i], attributes[i+1]));
			}
			out.append(">\n");
			for(Tag tag : subTags) {
				out.append(tag.toTreeString(depth+1));
			}
			if(hasClosingTag) out.append(String.format("%s%s\n%s</%s>\n", depthTab, content, depthTab, element));
			return out.toString();
		}
	}
}
