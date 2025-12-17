export function callChatMemory(query, conversationId) {
    return fetch(
        `/ai-server/advisor/memory/in/call?query=${encodeURIComponent(query)}&conversation_id=${encodeURIComponent(conversationId)}`
    )
    .then(res => res.text())
    .catch(err => {
        console.error('fetch请求异常:', err)
        throw err
    });
}