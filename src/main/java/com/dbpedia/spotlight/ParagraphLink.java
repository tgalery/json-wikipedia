/**
 *  Copyright 2015 DBpedia Spotlight
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.dbpedia.spotlight;

import it.cnr.isti.hpc.wikipedia.article.Link;

import java.util.List;

/*
 * Class to print the Paragraph links
 */
public class ParagraphLink {
	
	private String paraText;
	private List<Link> links;
	public String getParaText() {
		return paraText;
	}
	public void setParaText(String paraText) {
		this.paraText = paraText;
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	

}
