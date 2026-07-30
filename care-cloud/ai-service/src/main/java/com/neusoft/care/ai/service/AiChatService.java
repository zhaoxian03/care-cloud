package com.neusoft.care.ai.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

/**
 * LangChain4j AI Service接口，定义AI对话的核心契约
 *
 * 核心逻辑：
 * 1. 通过@SystemMessage注入SYSTEM_PROMPT，设定AI的角色和回答规则
 * 2. @MemoryId将sessionId与会话记忆绑定，不同sessionId隔离对话历史
 * 3. @UserMessage接收用户输入消息
 * 4. 返回TokenStream实现逐token的SSE流式推送
 *
 * 注意事项：
 * - TokenStream需要调用方消费（start/onNext/onComplete/onError），否则不会触发请求
 * - SYSTEM_PROMPT定义了"东软颐养中心养老健康助手"的角色和行为边界
 *
 * @author CareCenter Team
 */
public interface AiChatService {

    String SYSTEM_PROMPT = """
        你是一个专业的养老健康助手，服务于东软颐养中心的用户。
        你的职责包括：
        1. 健康咨询：回答关于老年常见病、慢性病管理的健康问题
        2. 护理建议：提供日常生活护理、康复训练的建议
        3. 营养指导：根据老人情况推荐合理的饮食方案
        4. 用药提醒：解释常见药物的用法和注意事项
        5. 心理关怀：提供情感支持和心理健康建议

        请使用温暖、耐心、专业的语气回复。
        不要给出任何可能被误解为医疗诊断的建议，必要时提醒咨询专业医生。
        """;

    /**
     * 流式AI对话方法
     *
     * @param sessionId 会话标识（通过@MemoryId绑定到ChatMemory）
     * @param message 用户输入消息
     * @return TokenStream流式响应，需调用方消费
     *
     * @SystemMessage 系统提示词
     * @MemoryId String sessionId —— 会话记忆绑定
     */
    @SystemMessage(SYSTEM_PROMPT)
    TokenStream chat(@MemoryId String sessionId, @UserMessage String message);
}
