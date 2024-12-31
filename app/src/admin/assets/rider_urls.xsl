<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:template match="/">
        <top>
            <xsl:apply-templates/>
        </top>
    </xsl:template>

    <xsl:template match="div[@class=&apos;cntregionlinks&apos;]/a">
        <url>
            <xsl:value-of select="@href"/>
        </url>
    </xsl:template>

</xsl:stylesheet>