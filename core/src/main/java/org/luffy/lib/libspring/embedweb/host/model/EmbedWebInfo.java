package org.luffy.lib.libspring.embedweb.host.model;

import java.util.Objects;

/**
 * 包含EWP的一些信息
 *
 * @author CJ
 */
public class EmbedWebInfo {
    private String name;
    private String version;
    private Package aPackage;
    private String uuid;
    private String privateResourceUri;
    private String pubicResourceUri;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EmbedWebInfo{");
        sb.append("name='").append(name).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", aPackage=").append(aPackage);
        sb.append(", privateResourceUri='").append(privateResourceUri).append('\'');
        sb.append(", pubicResourceUri='").append(pubicResourceUri).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmbedWebInfo that = (EmbedWebInfo) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(version, that.version) &&
                Objects.equals(aPackage, that.aPackage) &&
                Objects.equals(privateResourceUri, that.privateResourceUri) &&
                Objects.equals(pubicResourceUri, that.pubicResourceUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version, aPackage, privateResourceUri, pubicResourceUri);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Package getaPackage() {
        return aPackage;
    }

    public void setaPackage(Package aPackage) {
        this.aPackage = aPackage;
    }

    public String getPrivateResourceUri() {
        return privateResourceUri;
    }

    public void setPrivateResourceUri(String privateResourceUri) {
        this.privateResourceUri = privateResourceUri;
    }

    public String getPubicResourceUri() {
        return pubicResourceUri;
    }

    public void setPubicResourceUri(String pubicResourceUri) {
        this.pubicResourceUri = pubicResourceUri;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
