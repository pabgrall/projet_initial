package org.nuxeo.training.project;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("mystring")
public class MyString {
	@XNode("tva")
	public String tva;
}
