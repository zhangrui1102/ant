package com.etc.ant.elasticsearch.controller;

import com.etc.ant.common.core.model.request.SyncByTableRequest;
import com.etc.ant.common.core.model.response.Response;
import com.etc.ant.common.core.service.SyncService;
import com.etc.ant.common.core.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-26 22:44:00
 */
@Controller
@RequestMapping("/sync")
public class SyncController {
    private static final Logger logger = LoggerFactory.getLogger(SyncController.class);

    @Resource
    private SyncService syncService;

    /**
     * 通过库名和表名全量同步数据
     *
     * @param request 请求参数
     */
    @RequestMapping("/byTable")
    @ResponseBody
    public String syncTable(@Validated SyncByTableRequest request) {
        logger.debug("request_info: " + JSONUtils.convertObjectToJson(request));
        String response = Response.success(syncService.syncByTable(request)).toString();
        logger.debug("response_info: " + JSONUtils.convertObjectToJson(request));
        return response;
    }
}
