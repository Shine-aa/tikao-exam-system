/**
 * 全屏防切屏监控工具
 * 用于考试系统的全屏监控和切屏检测
 */

/**
 * 进入全屏
 * @param {HTMLElement} element - 要全屏的元素，默认为document.documentElement
 * @returns {Promise<boolean>} 是否成功进入全屏
 */
export const enterFullscreen = (element = document.documentElement) => {
  return new Promise((resolve) => {
    if (element.requestFullscreen) {
      element.requestFullscreen().then(() => {
        resolve(true)
      }).catch(() => {
        resolve(false)
      })
    } else if (element.webkitRequestFullscreen) {
      // Safari
      element.webkitRequestFullscreen()
      resolve(true)
    } else if (element.mozRequestFullScreen) {
      // Firefox
      element.mozRequestFullScreen()
      resolve(true)
    } else if (element.msRequestFullscreen) {
      // IE/Edge
      element.msRequestFullscreen()
      resolve(true)
    } else {
      resolve(false)
    }
  })
}

/**
 * 退出全屏
 * @returns {Promise<boolean>} 是否成功退出全屏
 */
export const exitFullscreen = () => {
  return new Promise((resolve) => {
    if (document.exitFullscreen) {
      document.exitFullscreen().then(() => {
        resolve(true)
      }).catch(() => {
        resolve(false)
      })
    } else if (document.webkitExitFullscreen) {
      document.webkitExitFullscreen()
      resolve(true)
    } else if (document.mozCancelFullScreen) {
      document.mozCancelFullScreen()
      resolve(true)
    } else if (document.msExitFullscreen) {
      document.msExitFullscreen()
      resolve(true)
    } else {
      resolve(false)
    }
  })
}

/**
 * 检查是否处于全屏状态
 * @returns {boolean} 是否处于全屏状态
 */
export const isFullscreen = () => {
  return !!(
    document.fullscreenElement ||
    document.webkitFullscreenElement ||
    document.mozFullScreenElement ||
    document.msFullscreenElement
  )
}

/**
 * 检查页面是否可见
 * @returns {boolean} 页面是否可见
 */
export const isPageVisible = () => {
  return !document.hidden
}

/**
 * 检查窗口是否有焦点
 * @returns {boolean} 窗口是否有焦点
 */
export const isWindowFocused = () => {
  return document.hasFocus()
}

/**
 * 创建全屏防切屏监控器
 * @param {Object} options - 配置选项
 * @param {number} options.maxAttempts - 最大切屏次数（已废弃，仅用于兼容，不会触发自动交卷）
 * @param {Function} options.onSwitchDetected - 切屏检测回调 (attempts: number, reason: string) => void
 * @param {Function} options.onMaxAttemptsReached - 达到最大次数回调 () => void（已废弃，不会触发）
 * @param {Function} options.onFullscreenChange - 全屏状态变化回调 (isFullscreen: boolean) => void
 * @returns {Object} 监控器对象，包含启动、停止等方法
 */
export const createFullscreenMonitor = (options = {}) => {
  const {
    maxAttempts = 999999, // 默认设置很大的值，实际上不会触发
    onSwitchDetected = () => {},
    onMaxAttemptsReached = () => {},
    onFullscreenChange = () => {}
  } = options

  let switchAttempts = 0
  let isMonitoring = false
  let lastVisibilityTime = Date.now()
  let lastFocusTime = Date.now()
  let isUserInteracting = false // 用户是否正在交互（防止误判）
  let checkTimer = null

  // 全屏状态变化监听
  const handleFullscreenChange = () => {
    const fullscreen = isFullscreen()
    onFullscreenChange(fullscreen)
    
    // 如果退出全屏且正在监控，记录切屏
    if (!fullscreen && isMonitoring) {
      recordSwitchAttempt('fullscreen_exit')
    }
  }

  // 页面可见性变化监听
  const handleVisibilityChange = () => {
    if (!isMonitoring) return
    
    const visible = isPageVisible()
    const now = Date.now()
    
    if (!visible) {
      // 页面隐藏，记录时间
      lastVisibilityTime = now
    } else {
      // 页面重新可见，检查是否切屏
      const timeDiff = now - lastVisibilityTime
      // 如果隐藏时间超过500ms，认为是切屏
      if (timeDiff > 500) {
        recordSwitchAttempt('visibility_change')
      }
    }
  }

  // 窗口焦点变化监听
  const handleFocusChange = () => {
    if (!isMonitoring) return
    
    const focused = isWindowFocused()
    const now = Date.now()
    
    if (!focused) {
      // 窗口失去焦点，记录时间
      lastFocusTime = now
    } else {
      // 窗口重新获得焦点，检查是否切屏
      const timeDiff = now - lastFocusTime
      // 如果失去焦点时间超过500ms，认为是切屏
      if (timeDiff > 500) {
        recordSwitchAttempt('focus_change')
      }
    }
  }

  // 窗口大小变化监听（检测开发者工具）
  const handleResize = () => {
    if (!isMonitoring) return
    
    // 如果窗口大小变化且不在全屏状态，可能是打开了开发者工具
    if (!isFullscreen()) {
      // 延迟检查，避免误判
      setTimeout(() => {
        if (!isFullscreen() && isMonitoring) {
          recordSwitchAttempt('resize')
        }
      }, 1000)
    }
  }

  // 记录切屏尝试
  const recordSwitchAttempt = (reason) => {
    // 如果用户正在交互（如点击、输入等），可能是误判，不记录
    if (isUserInteracting) {
      return
    }
    
    switchAttempts++
    onSwitchDetected(switchAttempts, reason)
    
    // 不再自动交卷，只进行警告
    // 注释掉自动交卷逻辑
    // if (switchAttempts >= maxAttempts) {
    //   onMaxAttemptsReached()
    //   stop()
    // }
  }

  // 用户交互监听（防止误判）
  const handleUserInteraction = () => {
    isUserInteracting = true
    // 500ms后重置，允许正常的交互
    setTimeout(() => {
      isUserInteracting = false
    }, 500)
  }

  // 定期检查全屏状态（防止用户通过其他方式退出全屏）
  const startPeriodicCheck = () => {
    if (checkTimer) {
      clearInterval(checkTimer)
    }
    
    checkTimer = setInterval(() => {
      if (!isFullscreen() && isMonitoring) {
        recordSwitchAttempt('periodic_check')
      }
    }, 2000) // 每2秒检查一次
  }

  // 停止定期检查
  const stopPeriodicCheck = () => {
    if (checkTimer) {
      clearInterval(checkTimer)
      checkTimer = null
    }
  }

  // 启动监控
  const start = () => {
    if (isMonitoring) return
    
    isMonitoring = true
    switchAttempts = 0
    lastVisibilityTime = Date.now()
    lastFocusTime = Date.now()
    
    // 添加事件监听
    document.addEventListener('fullscreenchange', handleFullscreenChange)
    document.addEventListener('webkitfullscreenchange', handleFullscreenChange)
    document.addEventListener('mozfullscreenchange', handleFullscreenChange)
    document.addEventListener('MSFullscreenChange', handleFullscreenChange)
    
    document.addEventListener('visibilitychange', handleVisibilityChange)
    window.addEventListener('blur', handleFocusChange)
    window.addEventListener('focus', handleFocusChange)
    window.addEventListener('resize', handleResize)
    
    // 监听用户交互
    document.addEventListener('mousedown', handleUserInteraction)
    document.addEventListener('keydown', handleUserInteraction)
    document.addEventListener('touchstart', handleUserInteraction)
    
    // 开始定期检查
    startPeriodicCheck()
  }

  // 停止监控
  const stop = () => {
    if (!isMonitoring) return
    
    isMonitoring = false
    
    // 移除事件监听
    document.removeEventListener('fullscreenchange', handleFullscreenChange)
    document.removeEventListener('webkitfullscreenchange', handleFullscreenChange)
    document.removeEventListener('mozfullscreenchange', handleFullscreenChange)
    document.removeEventListener('MSFullscreenChange', handleFullscreenChange)
    
    document.removeEventListener('visibilitychange', handleVisibilityChange)
    window.removeEventListener('blur', handleFocusChange)
    window.removeEventListener('focus', handleFocusChange)
    window.removeEventListener('resize', handleResize)
    
    document.removeEventListener('mousedown', handleUserInteraction)
    document.removeEventListener('keydown', handleUserInteraction)
    document.removeEventListener('touchstart', handleUserInteraction)
    
    // 停止定期检查
    stopPeriodicCheck()
  }

  // 重置计数器
  const reset = () => {
    switchAttempts = 0
  }

  // 获取当前切屏次数
  const getSwitchAttempts = () => {
    return switchAttempts
  }

  // 获取是否正在监控
  const getIsMonitoring = () => {
    return isMonitoring
  }

  return {
    start,
    stop,
    reset,
    getSwitchAttempts,
    getIsMonitoring,
    enterFullscreen,
    exitFullscreen,
    isFullscreen
  }
}

