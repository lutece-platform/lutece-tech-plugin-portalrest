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
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;

//import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.message.SiteMessageException;

import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


/**
 * Portlet resource
 */
@Path( RestConstants.BASE_PATH + Constants.PLUGIN_NAME + "/portlet" )
public class PortletRest
{
    @GET
    @Path( "/{id}" )
    @Produces( MediaType.APPLICATION_XML )
    public String getPortlet( @PathParam( "id" )
    String strId )
    {
        StringBuilder sbXML = new StringBuilder(  );

        try
        {
            int nId = Integer.parseInt( strId );
            Portlet portlet = PortletHome.findByPrimaryKey( nId );

            if ( portlet != null )
            {
                sbXML.append( "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" );
                addPortletXml( sbXML, portlet );
            }
        }
        catch ( NumberFormatException e )
        {
            sbXML.append( XMLUtil.formatError( "Invalid portlet number", 3 ) );
        }
        catch ( Exception e )
        {
            sbXML.append( XMLUtil.formatError( "Portlet not found", 1 ) );
        }

        return sbXML.toString(  );
    }

    @GET
    @Path( "/{id}" )
    @Produces( MediaType.APPLICATION_JSON )
    public String getPortletJson( @PathParam( "id" )
    String strId )
    {
        String strJSON = "";

        try
        {
            int nId = Integer.parseInt( strId );
            Portlet portlet = PortletHome.findByPrimaryKey( nId );

            if ( portlet != null )
            {
                JSONObject json = new JSONObject(  );
                json.accumulate( RestPortletConstants.ID_PORTLET, portlet.getId(  ) );
                json.accumulate( RestPortletConstants.NAME, portlet.getName(  ) );
                json.accumulate( RestPortletConstants.ID_PAGE, portlet.getPageId(  ) );
                json.accumulate( RestPortletConstants.TYPE_PORTLET, portlet.getPortletTypeId(  ) );
                json.accumulate( RestPortletConstants.ID_STYLE, portlet.getStyleId(  ) );
                json.accumulate( RestPortletConstants.COLUMN, portlet.getColumn(  ) );
                json.accumulate( RestPortletConstants.ORDER, portlet.getOrder(  ) );
                json.accumulate( RestPortletConstants.ACCEPT_ALIAS, portlet.getAcceptAlias(  ) );
                json.accumulate( RestPortletConstants.DISPLAY_PORTLET_TITLE, portlet.getDisplayPortletTitle(  ) );
                json.accumulate( RestPortletConstants.DISPLAY_PORTLET_CONTENT, getPortletContent( portlet ) );

                JSONObject jsonPortlet = new JSONObject(  );
                jsonPortlet.accumulate( "portlet", json );
                strJSON = jsonPortlet.toString( 4 );
            }
        }
        catch ( NumberFormatException e )
        {
            strJSON = JSONUtil.formatError( "Invalid portlet number", 3 );
        }
        catch ( Exception e )
        {
            strJSON = JSONUtil.formatError( "Portlet not found", 1 );
        }

        return strJSON;
    }

    @GET
    @Path( "" )
    @Produces( MediaType.APPLICATION_XML )
    public String getPortletsXml( @QueryParam( "id_page" )
    String strPageId, @QueryParam( "type_portlet" )
    String strPortletType )
    {
        String strXML = "";

        try
        {
            List<Portlet> list = null;

            if ( strPortletType != null )
            {
                list = PortletHome.findByType( strPortletType );
            }
            else if ( strPageId != null )
            {
                int nIdPage = Integer.parseInt( strPageId );
                Page page = PageHome.findByPrimaryKey( nIdPage );
                list = page.getPortlets(  );
            }

            strXML = getXML( list );
        }
        catch ( Exception e )
        {
            strXML = XMLUtil.formatError( "Portlet not found", 1 );
        }

        return strXML;
    }

    private String getXML( List<Portlet> list )
    {
        StringBuilder sbXML = new StringBuilder(  );
        sbXML.append( "<?xml version=\"1.0\"?>\n" );

        sbXML.append( "<portlets>\n " );

        for ( Portlet portlet : list )
        {
            addPortletXml( sbXML, portlet );
        }

        sbXML.append( "</portlets>\n " );

        return sbXML.toString(  );
    }

    private void addPortletXml( StringBuilder sbXML, Portlet portlet )
    {
        sbXML.append( "<portlet>" );
        sbXML.append( "<portlet-name>" ).append( portlet.getName(  ) ).append( "</portlet-name>\n" );
        sbXML.append( "<portlet-id>" ).append( portlet.getId(  ) ).append( "</portlet-id>\n" );
        sbXML.append( "<page-id>" ).append( portlet.getPageId(  ) ).append( "</page-id>\n" );
        sbXML.append( "<display-portlet-title>" ).append( portlet.getDisplayPortletTitle(  ) )
             .append( "</display-portlet-title>\n" );
        sbXML.append( "<column>" ).append( portlet.getColumn(  ) ).append( "</column>\n" );
        sbXML.append( "<order>" ).append( portlet.getOrder(  ) ).append( "</order>\n" );
        sbXML.append( "<portlet-type>" ).append( portlet.getPortletTypeId(  ) ).append( "</portlet-type>\n" );
        sbXML.append( "<style-id>" ).append( portlet.getStyleId(  ) ).append( "</style-id>\n" );
        sbXML.append( "<status>" ).append( portlet.getStatus(  ) ).append( "</status>\n" );
        sbXML.append( "<content><![CDATA[" ).append( getPortletContent( portlet ) ).append( "]]></content>\n" );
        sbXML.append( "</portlet>" );
    }

    private String getPortletContent( Portlet portlet )
    {
        try
        {
            XmlTransformerService xmlTransformerService = new XmlTransformerService(  );
            String strPortletXmlContent = portlet.getXml( null );
            String strXslUniqueId = String.valueOf( portlet.getStyleId(  ) );
            String strPortletContent = xmlTransformerService.transformBySourceWithXslCache( strPortletXmlContent,
                    portlet.getXslSource( 0 ), strXslUniqueId, new HashMap<String, String>(  ) );

            strPortletContent = removeXmlHeader( strPortletContent );

            return strPortletContent;
        }
        catch ( SiteMessageException ex )
        {
            Logger.getLogger( PortletRest.class.getName(  ) ).log( Level.SEVERE, null, ex );
        }

        return "Error while retrieving portlet content";
    }

    private String removeXmlHeader( String strSource )
    {
        String strDest = strSource;
        int nPos = strDest.indexOf( "?>" );

        if ( nPos > 0 )
        {
            strDest = strDest.substring( nPos + 2 );
        }

        return strDest;
    }
}
