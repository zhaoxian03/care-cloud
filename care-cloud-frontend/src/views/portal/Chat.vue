<template>
  <div class="chat-wrap">
    <div class="chat-messages" ref="msgBox">
      <div v-for="(m, i) in messages" :key="i" :class="['msg', m.role === 'user' ? 'user' : 'ai']">
        <div class="bubble" v-text="m.content"></div>
      </div>
      <div v-if="loading" class="msg ai"><div class="bubble">AI正在思考...</div></div>
    </div>
    <div class="chat-input">
      <el-input v-model="input" placeholder="描述您的身体状况和需求，如：我关节不好，需要康复护理" @keydown.enter="send" :disabled="loading" size="large">
        <template #append>
          <el-button :disabled="loading || !input.trim()" @click="send" type="primary">发送</el-button>
        </template>
      </el-input>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { useUserStore } from '../../store/user'

const store = useUserStore()

const msgBox = ref(null)
const messages = ref([{ role: 'ai', content: '您好！我是健康助手，请描述您的身体状况或需求，我将为您推荐合适的服务和护理项目。' }])
const input = ref('')
const loading = ref(false)

const deviceId = localStorage.getItem('ai_device_id') || crypto.randomUUID()
localStorage.setItem('ai_device_id', deviceId)
const customerId = store.customerInfo?.id || 'guest'
const sessionId = `${customerId}_${deviceId}`

async function send() {
  const text = input.value.trim()
  if (!text || loading.value) return
  input.value = ''
  messages.value.push({ role: 'user', content: text })
  const aiIdx = messages.value.length
  messages.value.push({ role: 'ai', content: '' })
  loading.value = true

  try {
    const resp = await fetch(`/api/app/assistant?sessionId=${sessionId}`, {
      method: 'POST',
      headers: { 'Content-Type': 'text/plain;charset=UTF-8' },
      body: text
    })
    if (!resp.ok) throw new Error(resp.status)
    const reader = resp.body.getReader()
    const dec = new TextDecoder()
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      messages.value[aiIdx].content += dec.decode(value, { stream: true })
      await nextTick()
      msgBox.value.scrollTop = msgBox.value.scrollHeight
    }
  } catch {
    messages.value[aiIdx].content = '抱歉，AI服务暂时不可用。'
  } finally {
    loading.value = false
  }
}

onMounted(() => { msgBox.value.scrollTop = msgBox.value.scrollHeight })
</script>

<style scoped>
.chat-wrap { display: flex; flex-direction: column; height: calc(100vh - 120px); max-width: 800px; margin: 0 auto; }
.chat-messages { flex: 1; overflow-y: auto; padding: 16px; background: #fff; border-radius: 8px; margin-bottom: 12px; }
.msg { display: flex; margin-bottom: 14px; }
.msg.user { justify-content: flex-end; }
.bubble { max-width: 75%; padding: 10px 14px; border-radius: 10px; line-height: 1.6; font-size: 14px; white-space: pre-wrap; }
.msg.user .bubble { background: #95ec69; border-bottom-right-radius: 4px; }
.msg.ai .bubble { background: #f0f0f0; border-bottom-left-radius: 4px; }
.chat-input { background: #fff; padding: 12px; border-radius: 8px; }
</style>
