/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id$ */

package org.apache.xmlgraphics.image.loader;

/**
 * Special image flavor subclass which enables the restriction to a particular XML namespace.
 */
public class XMLNamespaceEnabledImageFlavor extends RefinedImageFlavor {

    private String namespace;

    /**
     * Constructs a new image flavor.
     * @param parentFlavor the parent image flavor
     * @param namespace an XML namespace URI refining the parent image flavor
     */
    public XMLNamespaceEnabledImageFlavor(ImageFlavor parentFlavor, String namespace) {
        super(parentFlavor.getName() + ";namespace=" + namespace, parentFlavor);
        this.namespace = namespace;
    }

    /**
     * Returns the XML namespace URI associated with the image flavor.
     * @return the XML namespace URI
     */
    public String getNamespace() {
        return this.namespace;
    }

}