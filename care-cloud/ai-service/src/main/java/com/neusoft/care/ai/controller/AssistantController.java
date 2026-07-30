package com.neusoft.care.ai.controller;

import com.neusoft.care.ai.service.AiAssistantService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * 智能助手REST接口，接收前端请求并返回SSE流式响应
 *
 * 核心逻辑：
 * 1. 接收前端传递的sessionId（默认为"default"），用于区分不同用户的会话
 * 2. 调用AiAssistantService进行RAG检索增强的推荐流程
 * 3. 通过Flux返回SSE（Server-Sent Events）流式数据，实现打字机效果
 *
 * 注意事项：
 * - sessionId为"default"时，所有匿名用户共享同一个会话记忆
 * - 前端需通过EventSource或fetch API消费SSE流
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/app")
public class AssistantController {

    private final AiAssistantService assistantService;

    /**
     * 构造函数，注入推荐服务
     *
     * @param assistantService AI助手服务
     */
    public AssistantController(AiAssistantService assistantService) {
        this.assistantService = assistantService;
    }

    /**
     * 智能推荐接口，返回SSE流式响应
     *
     * @param sessionId 会话标识，默认为"default"
     * @param message 用户输入的需求描述
     * @return Flux流式字符串，每个token作为一次事件推送给前端
     */
    @PostMapping("/assistant")
    public Flux<String> recommend(@RequestParam(defaultValue = "default") String sessionId,
                                   @RequestBody String message) {
        return assistantService.recommend(sessionId, message);
    }
}
