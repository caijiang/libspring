package me.jiangcai.lib.git.bean;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author CJ
 */
@Data
@Component
public class GitRepositoryState {
    @Value("${git.tags}")
    private String tags;                    // =${git.tags} // comma separated tag names
    @Value("${git.branch}")
    private String branch;                  // =${git.branch}
    @Value("${git.dirty}")
    private String dirty;                   // =${git.dirty}
    @Value("${git.remote.origin.url}")
    private String remoteOriginUrl;         // =${git.remote.origin.url}

    @Value("${git.commit.id}")
    private String commitId;                // =${git.commit.id.full} OR ${git.commit.id}
    @Value("${git.commit.id.abbrev}")
    private String commitIdAbbrev;          // =${git.commit.id.abbrev}
    @Value("${git.commit.id.describe}")
    private String describe;                // =${git.commit.id.describe}
    @Value("${git.commit.id.describe-short}")
    private String describeShort;           // =${git.commit.id.describe-short}
    @Value("${git.commit.user.name}")
    private String commitUserName;          // =${git.commit.user.name}
    @Value("${git.commit.user.email}")
    private String commitUserEmail;         // =${git.commit.user.email}
    @Value("${git.commit.message.full}")
    private String commitMessageFull;       // =${git.commit.message.full}
    @Value("${git.commit.message.short}")
    private String commitMessageShort;      // =${git.commit.message.short}
    @Value("${git.commit.time}")
    private String commitTime;              // =${git.commit.time}
    @Value("${git.closest.tag.name}")
    private String closestTagName;          // =${git.closest.tag.name}
    @Value("${git.closest.tag.commit.count}")
    private String closestTagCommitCount;   // =${git.closest.tag.commit.count}

    @Value("${git.build.user.name}")
    private String buildUserName;           // =${git.build.user.name}
    @Value("${git.build.user.email}")
    private String buildUserEmail;          // =${git.build.user.email}
    @Value("${git.build.time}")
    private String buildTime;               // =${git.build.time}
    @Value("${git.build.host}")
    private String buildHost;               // =${git.build.host}
    @Value("${git.build.version}")
    private String buildVersion;             // =${git.build.version}

    /**
     * @return 获取可访问的commit url
     */
    public String getCommitUrl() {
        return remoteOriginUrl + "/commit/" + commitId;
    }
}
