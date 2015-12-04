/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.template.data;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TemplateApiConstants {


    public static final String TEMPLATE_RESOURCE_NAME= "template";

    public static final String documentName  = "name";

    public static final String documentType = "type";

    public static final String entity = "entity";

    public static final String templateText = "text";

    public static final String mappers = "mappers";


    public static final Set<String>  supportedParams = new HashSet<String>(Arrays.asList(documentName,documentType,
            entity,templateText,mappers));


}
