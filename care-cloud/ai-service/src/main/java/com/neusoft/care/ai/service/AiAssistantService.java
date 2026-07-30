package com.neusoft.care.ai.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * AI智能推荐服务，串联RAG检索与AI对话的完整推荐流程
 *
 * 核心逻辑：
 * 1. 将用户消息送入AiRagService进行RAG检索，获取匹配的服务/护理项目作为上下文
 * 2. 若有匹配结果，构建包含检索上下文的增强Prompt，要求AI推荐1-3个最合适的项目
 * 3. 若无匹配结果，直接使用原始消息作为prompt
 * 4. 调用AiChatService的流式接口，将TokenStream转为Flux<String>返回
 *
 * 注意事项：
 * - 检索增强的Prompt会要求AI说明推荐原因、价格和预期效果
 * - 通过Flux.create将TokenStream的回调事件桥接到响应式流
 *
 * @author CareCenter Team
 */
@Service
public class AiAssistantService {

    private final AiRagService ragService;
    private final AiChatService chatService;

    /**
     * 构造函数，注入RAG服务和聊天服务
     *
     * @param ragService RAG检索服务
     * @param chatService AI对话服务
     */
    public AiAssistantService(AiRagService ragService, AiChatService chatService) {
        this.ragService = ragService;
        this.chatService = chatService;
    }

    /**
     * 执行RAG增强的智能推荐
     *
     * @param sessionId 会话标识，用于隔离对话记忆
     * @param message 用户输入的需求描述
     * @return Flux流式响应，逐token返回AI推荐结果
     */
    public Flux<String> recommend(String sessionId, String message) {

        // Step 1: 检索
        String context = ragService.searchAndBuildContext(message);
        // Step 2: 增强 Prompt
        String prompt;
        if (context != null && !context.isEmpty()) {
            prompt = String.format("""
                用户描述了以下需求和身体状况：\n%s\n\n%s
                请根据用户的需求，从匹配结果中推荐最合适的1-3个服务或护理项目，
                说明推荐原因、价格和预期效果。如果没有合适匹配，如实告知并给出建议。""",
                message, context);
        } else {
            prompt = message;
        }
        // Step 3: 流式生成
        return Flux.create(sink ->
            chatService.chat(sessionId, prompt)   // 每生成一个 Token，就推给前端
                .onNext(sink::next)               // 生成完成，结束流
                .onComplete(r -> sink.complete()) // 发生异常，将错误传给前端
                .onError(sink::error)             // 启动流式生成
                .start()
        );
    }
}
