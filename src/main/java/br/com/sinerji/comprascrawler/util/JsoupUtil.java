package br.com.sinerji.comprascrawler.util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class JsoupUtil {

	public static String getElementValueById(Element document, String id) {
		Element element = document.getElementById(id);
		if (element == null) {
			throw new IllegalStateException("Element with id " + id + " not found in HTML document");
		}
		
		return element.val();
	}
	
	public static Element getFirstElementByTag(Element document, String tag) {
		Elements elements = document.getElementsByTag(tag);
		if (elements.isEmpty()) {
			throw new RuntimeException("Element with tag " + tag + " not found in html document");
		}
		
		return elements.first();
	}
	
	public static Element getFirstElementByClass(Document document, String className) {
		Elements elements = document.getElementsByClass(className);
		if (elements.isEmpty()) {
			throw new RuntimeException("Element with class " + className + " not found in html document");
		}
		
		return elements.first();
	}
}
