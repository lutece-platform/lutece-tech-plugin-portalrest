/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
import fr.paris.lutece.plugins.rest.util.json.JSONUtil;
import fr.paris.lutece.plugins.rest.util.xml.XMLUtil;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.util.xml.XmlUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;


/**
 * Page resource
 */
@Path( RestConstants.BASE_PATH + Constants.PLUGIN_NAME + "/page" )
public class PageRest
{
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_ID_PARENT = "id_parent";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_ID_TEMPLATE = "id_template";
    private static final String KEY_THEME = "theme";
    private static final String KEY_ORDER = "order";
    private static final String KEY_META_DESCRIPTION = "meta_description";
    private static final String KEY_META_KEYWORDS = "meta_keywords";
    private static final String KEY_ROLE = "role";
    private static final String KEY_PAGE = "page";
    private static final String KEY_PAGES = "pages";
    private static final int INDENT = 4;

    @GET
    @Path( "/{id}" )
    @Produces( MediaType.APPLICATION_XML )
    public String getPageXml( @PathParam( "id" )
    String strId )
    {
        String strXML = "";

        try
        {
            int nId = Integer.parseInt( strId );
            Page page = PageHome.findByPrimaryKey( nId );

            if ( page != null )
            {
                strXML = getXmlPage( page, true );
            }
        }
        catch ( NumberFormatException e )
        {
            strXML = XMLUtil.formatError( "Invalid page number", 3 );
        }
        catch ( Exception e )
        {
            strXML = XMLUtil.formatError( "Page not found", 1 );
        }

        return strXML;
    }

    @GET
    @Path( "/{id}" )
    @Produces( MediaType.APPLICATION_JSON )
    public String getPageJson( @PathParam( KEY_ID )
    String strId ) throws SiteMessageException
    {
        String strJSON = "";

        try
        {
            int nId = Integer.parseInt( strId );
            Page page = PageHome.findByPrimaryKey( nId );

            if ( page != null )
            {
                JSONObject jsonPage = getJson( page );
                strJSON = jsonPage.toString( 4 );
            }
        }
        catch ( NumberFormatException e )
        {
            strJSON = JSONUtil.formatError( "Invalid page number", 3 );
        }
        catch ( Exception e )
        {
            strJSON = JSONUtil.formatError( "Page not found", 1 );
        }

        return strJSON;
    }

    @GET
    @Path( "" )
    @Produces( MediaType.APPLICATION_XML )
    public String getPagesXml( @QueryParam( KEY_ID_PARENT )
    String strParentId, @QueryParam( "format" )
    String strFormat, @Context
    HttpServletResponse response )
    {
        if ( ( strFormat != null ) && ( strFormat.equalsIgnoreCase( "json" ) ) )
        {
            response.setContentType( MediaType.APPLICATION_JSON );

            return getPagesJson( strParentId );
        }

        String strXML = "";

        try
        {
            strXML = getPagesXML( getPages( strParentId ) );
        }
        catch ( Exception e )
        {
            strXML = XMLUtil.formatError( "Page not found", 1 );
        }

        return strXML;
    }

    @GET
    @Path( "" )
    @Produces( MediaType.APPLICATION_JSON )
    public String getPagesJson( @QueryParam( KEY_ID_PARENT )
    String strParentId )
    {
        return getPagesJson( strParentId );
    }

    @POST
    @Produces( MediaType.TEXT_HTML )
    @Consumes( MediaType.APPLICATION_FORM_URLENCODED )
    public String createPage( @FormParam( "id_page" )
    String strId, @FormParam( "id_parent" )
    String strParentId, @FormParam( "name" )
    String strName, @FormParam( "description" )
    String strDescription, @FormParam( "role" )
    String strRole, @FormParam( "theme" )
    String strTheme, @FormParam( "order" )
    String strOrder, @FormParam( "id_template" )
    String strTemplateId, @FormParam( "meta_keywords" )
    String strMetaKeywords, @FormParam( "meta_description" )
    String strMetaDescription )
    {
        String strReturn = "";

        try
        {
            boolean bUpdate = ( strId != null );
            Page page = null;

            if ( bUpdate )
            {
                page = PageHome.findByPrimaryKey( Integer.parseInt( strId ) );
            }
            else
            {
                page = new Page(  );
            }

            strDescription = getNotNull( strDescription, "" );
            strRole = getNotNull( strRole, "none" );
            strMetaKeywords = getNotNull( strMetaKeywords, "" );
            strMetaDescription = getNotNull( strMetaDescription, "" );
            strOrder = getNotNull( strOrder, "1" );
            strTheme = getNotNull( strTheme, "default" );

            page.setParentPageId( Integer.parseInt( strParentId ) );
            page.setName( strName );
            page.setDescription( strDescription );
            page.setRole( strRole );
            page.setCodeTheme( strTheme );
            page.setMetaKeywords( strMetaKeywords );
            page.setMetaDescription( strMetaDescription );
            page.setOrder( Integer.parseInt( strOrder ) );
            page.setPageTemplateId( Integer.parseInt( strTemplateId ) );

            if ( bUpdate )
            {
                PageHome.update( page );
                strReturn = "" + page.getId(  );
            }
            else
            {
                Page p = PageHome.create( page );
                strReturn = "" + p.getId(  );
            }
        }
        catch ( Exception e )
        {
            strReturn = "Error creating or updating page : " + e.getMessage(  );
        }

        return strReturn;
    }

    private Collection<Page> getPages( String strParentId )
    {
        Collection<Page> list = null;

        if ( strParentId != null )
        {
            int nParentPageId = Integer.parseInt( strParentId );
            list = PageHome.getChildPagesMinimalData( nParentPageId );
        }
        else
        {
            list = PageHome.getAllPages(  );
        }

        return list;
    }

    private String getPagesXML( Collection<Page> list )
    {
        StringBuffer sbXML = new StringBuffer(  );
        sbXML.append( "<?xml version=\"1.0\"?>\n" );

        XmlUtil.beginElement( sbXML, KEY_PAGES );

        for ( Page page : list )
        {
            sbXML.append( getXmlPage( page, false ) );
        }

        XmlUtil.endElement( sbXML, KEY_PAGES );

        return sbXML.toString(  );
    }

    private String getXmlPage( Page page, boolean bHeader )
    {
        StringBuffer sbXML = new StringBuffer(  );

        if ( bHeader )
        {
            sbXML.append( "<?xml version=\"1.0\"?>\n" );
        }

        XmlUtil.beginElement( sbXML, KEY_PAGE );
        XmlUtil.addElement( sbXML, KEY_ID, page.getId(  ) );
        XmlUtil.addElement( sbXML, KEY_NAME, page.getName(  ) );
        XmlUtil.addElement( sbXML, KEY_DESCRIPTION, page.getDescription(  ) );
        XmlUtil.addElement( sbXML, KEY_ID_PARENT, page.getParentPageId(  ) );
        XmlUtil.addElement( sbXML, KEY_ROLE, page.getRole(  ) );
        XmlUtil.addElement( sbXML, KEY_ID_TEMPLATE, page.getPageTemplateId(  ) );
        XmlUtil.addElement( sbXML, KEY_ORDER, page.getOrder(  ) );
        XmlUtil.addElement( sbXML, KEY_THEME, page.getCodeTheme(  ) );
        XmlUtil.addElement( sbXML, KEY_META_DESCRIPTION, page.getMetaDescription(  ) );
        XmlUtil.addElement( sbXML, KEY_META_KEYWORDS, page.getMetaKeywords(  ) );
        XmlUtil.endElement( sbXML, KEY_PAGE );

        return sbXML.toString(  );
    }

    private String getPagesAsJson( String strParentId )
    {
        Collection<Page> list = getPages( strParentId );
        JSONArray jsonPages = new JSONArray(  );

        for ( Page page : list )
        {
            jsonPages.add( getJson( page ) );
        }

        return jsonPages.toString( INDENT );
    }

    private JSONObject getJson( Page page )
    {
        JSONObject json = new JSONObject(  );
        json.accumulate( KEY_ID, page.getId(  ) );
        json.accumulate( KEY_NAME, page.getName(  ) );
        json.accumulate( KEY_DESCRIPTION, page.getDescription(  ) );
        json.accumulate( KEY_ID_PARENT, page.getParentPageId(  ) );
        json.accumulate( KEY_ROLE, page.getRole(  ) );
        json.accumulate( KEY_ID_TEMPLATE, page.getPageTemplateId(  ) );
        json.accumulate( KEY_ORDER, page.getOrder(  ) );
        json.accumulate( KEY_THEME, page.getCodeTheme(  ) );
        json.accumulate( KEY_META_DESCRIPTION, page.getMetaDescription(  ) );
        json.accumulate( KEY_META_KEYWORDS, page.getMetaKeywords(  ) );

        JSONObject jsonPage = new JSONObject(  );
        jsonPage.accumulate( KEY_PAGE, json );

        return jsonPage;
    }

    private String getNotNull( String strSrc, String strDefault )
    {
        return ( strSrc != null ) ? strSrc : strDefault;
    }
}
