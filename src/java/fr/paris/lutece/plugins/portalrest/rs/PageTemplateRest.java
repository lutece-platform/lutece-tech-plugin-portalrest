/*
 * Copyright (c) 2002-2017, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.portalrest.rs;

import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.portal.business.style.PageTemplate;
import fr.paris.lutece.portal.business.style.PageTemplateHome;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * Role resource
 */
@Path( RestConstants.BASE_PATH + Constants.PLUGIN_NAME + "/pagetemplate" )
public class PageTemplateRest
{
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PAGE_TEMPLATE = "page-template";
    private static final String KEY_PAGE_TEMPLATES = "page-templates";

    @GET
    @Path( "/" )
    @Produces( MediaType.APPLICATION_XML )
    public String getPageTemplateXml(  )
    {
        List<PageTemplate> list = PageTemplateHome.getPageTemplatesList(  );
        StringBuffer sbXML = new StringBuffer(  );
        sbXML.append( "<?xml version=\"1.0\"?>\n" );
        XmlUtil.beginElement( sbXML, KEY_PAGE_TEMPLATES );

        for ( PageTemplate page : list )
        {
            XmlUtil.beginElement( sbXML, KEY_PAGE_TEMPLATE );
            XmlUtil.addElement( sbXML, KEY_ID, page.getId(  ) );
            XmlUtil.addElement( sbXML, KEY_NAME, page.getDescription(  ) );
            XmlUtil.endElement( sbXML, KEY_PAGE_TEMPLATE );
        }

        XmlUtil.endElement( sbXML, KEY_PAGE_TEMPLATES );

        return sbXML.toString(  );
    }
}
