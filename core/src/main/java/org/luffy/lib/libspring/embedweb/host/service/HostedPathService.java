package org.luffy.lib.libspring.embedweb.host.service;

import org.luffy.lib.libspring.embedweb.EmbedWeb;
import org.luffy.lib.libspring.embedweb.PathService;

import java.util.Map;

/**
 * @author CJ
 */
public interface HostedPathService extends PathService {

    Map<EmbedWeb, String> webUUIDs();

}
