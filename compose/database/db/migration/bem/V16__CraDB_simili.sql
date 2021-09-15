CREATE TABLE crl
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    url          VARCHAR(2000)         NOT NULL,
    lastmodified BIGINT                NOT NULL,
    nextupdate   BIGINT                NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE revoked
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    crlid        BIGINT                NOT NULL,
    serialnumber VARCHAR(48),
    added        BIGINT                NOT NULL,
    since        BIGINT                NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (crlid) REFERENCES crl (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
CREATE UNIQUE INDEX revoked_crl ON revoked (crlid, serialnumber);
