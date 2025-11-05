import request from '@/utils/request';

/**
 * 发送聊天消息给后端 AI 接口
 * @param {object} data 包含 userId 和 message
 * @returns {Promise}
 */
export function postChatMessage(data) {
  return request({
    url: '/api/ai/memory/chat',
    method: 'post',
    data,
    timeout: 60000 // 单次聊天允许最多 60 秒
  });
}
