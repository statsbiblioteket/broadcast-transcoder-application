<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:foxml="info:fedora/fedora-system:def/foxml#"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:model="info:fedora/fedora-system:def/model#">


    <xsl:strip-space elements="*"/>



    <xsl:template match="/">
        <BTAdata>
            <xsl:apply-templates/>
        </BTAdata>
    </xsl:template>



    <xsl:template match="//foxml:digitalObject">
        <xsl:variable name="pid" select="@PID"/>
        <xsl:variable name="label" select="foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#label']/@VALUE"/>
        <xsl:variable name="program" select="foxml:datastream[@ID='RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description/model:hasModel[@rdf:resource='info:fedora/doms:ContentModel_Program']"/>
        <xsl:variable name="file" select="foxml:datastream[@ID='RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description/model:hasModel[@rdf:resource='info:fedora/doms:ContentModel_RadioTVFile']"/>

        <xsl:if test="$program">
            <program>
                <xsl:attribute name="id">
                    <xsl:value-of select="$pid"/>
                </xsl:attribute>
                <xsl:apply-templates select="foxml:datastream">
                    <xsl:with-param name="program" select="$program"/>
                </xsl:apply-templates>
            </program>

        </xsl:if>
        <xsl:if test="$file">
            <file>
                <xsl:attribute name="id">
                    <xsl:value-of select="$label"/>
                </xsl:attribute>
                <xsl:apply-templates select="foxml:datastream">
                    <xsl:with-param name="file" select="$file"/>
                </xsl:apply-templates>
            </file>
        </xsl:if>

    </xsl:template>

    <xsl:template name="getContent">
        <xsl:copy-of select="current()/foxml:datastreamVersion[last()]/foxml:xmlContent/*"/>
    </xsl:template>

    <xsl:template match="foxml:datastream[@ID='PROGRAM_BROADCAST']">
        <xsl:param name="program"/>
        <xsl:if test="$program">
            <xsl:call-template name="getContent"/>
        </xsl:if>
    </xsl:template>

<!--
    <xsl:template match="foxml:datastream[@ID='PBCORE']">
        <xsl:param name="program"/>
        <xsl:if test="$program">
            <pbcore>
                <xsl:call-template name="getContent"/>
            </pbcore>
        </xsl:if>
    </xsl:template>
-->



    <xsl:template match="foxml:datastream[@ID='BROADCAST_METADATA']">
        <xsl:param name="file"/>
        <xsl:if test="$file">
            <broadcast_metadata>
                <xsl:call-template name="getContent"/>
            </broadcast_metadata>
        </xsl:if>
    </xsl:template>

    <xsl:template match="foxml:datastream[@ID='FFPROBE']">
        <xsl:param name="file"/>
        <xsl:if test="$file">
            <ffprobe>
                <xsl:call-template name="getContent"/>
            </ffprobe>
        </xsl:if>
    </xsl:template>

    <xsl:template match="foxml:datastream[@ID='FFPROBE_ERROR_LOG']">
        <xsl:param name="file"/>
        <xsl:if test="$file">
            <ffprobe_errorLog>
                <xsl:call-template name="getContent"/>
            </ffprobe_errorLog>
        </xsl:if>
    </xsl:template>


<!--
    <xsl:template match="foxml:datastream[@ID='CHARACTERISATION']">
        <xsl:param name="file"/>
        <xsl:if test="$file">
            <characterisation>
                <xsl:call-template name="getContent"/>
            </characterisation>
        </xsl:if>
    </xsl:template>
-->


    <xsl:template match="foxml:datastream">

    </xsl:template>


</xsl:stylesheet>
