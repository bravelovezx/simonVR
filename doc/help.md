# 鼠标滑动显示浮动工具栏
## 🎯 一、整体目标

用户在阅读文章时，可以通过鼠标选中文本，然后弹出一个浮动工具栏（`selection-toolbar`），提供以下功能：

- 🔍 查词
- ✏️ 批注
- ⭐ 收藏
- ❌ 取消显示

同时，用户如果点击页面其他位置，应该取消选中状态（即隐藏工具栏）。

---

## 🧩 二、功能模块划分

我们可以将整个功能划分为以下几个关键模块：

| 模块 | 功能描述 |
|------|----------|
| 文章展示 | 显示当前选中的文章标题和内容 |
| 文字选中 | 监听鼠标释放事件，获取选中文字 |
| 工具栏显示 | 根据选中文字的位置显示浮动工具栏 |
| 功能按钮 | 提供查词、批注、收藏等操作 |
| 点击外部区域关闭 | 点击非选中区域时隐藏工具栏 |

---

## 🧠 三、关键技术点详解

### 1. 监听选中文字：`@mouseup="handleTextSelection"`

```html
<div @mouseup="handleTextSelection">
```

当用户用鼠标拖动选择文本后松开鼠标左键时，会触发 `handleTextSelection` 方法。

#### 📌 方法逻辑：
```js
const handleTextSelection = (e) => {
  const selection = window.getSelection()
  if (!selection.toString().trim()) return // 如果没有选中内容则返回

  selectedText.value = selection.toString() // 保存选中的文字
  showToolbar.value = true // 显示工具栏
  toolbarPos.x = e.clientX // 设置工具栏 X 坐标
  toolbarPos.y = e.clientY - 40 // 设置工具栏 Y 坐标
  isSelecting.value = true // 标记为正在选择
}
```

> ✅ 这里使用了浏览器原生 API `window.getSelection()` 获取用户当前选中的文字内容。

---

### 2. 显示浮动工具栏

```html
<div v-if="showToolbar" class="selection-toolbar" :style="{ left: toolbarPos.x + 'px', top: toolbarPos.y + 'px' }">
```

根据 `showToolbar` 控制是否显示工具栏，并通过 `toolbarPos` 设置其位置。

---

### 3. 实现点击空白处隐藏工具栏

这部分是你的核心需求之一。

#### 🧮 实现思路：

- 用户点击页面任意地方。
- 判断是否是在“选中行为”之后触发的点击。
- 如果不是选中行为，就隐藏工具栏。

#### ✅ 具体实现：

```js
const handleClickOutside = (e) => {
  if (!isSelecting.value) {
    showToolbar.value = false
  }
  isSelecting.value = false
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
```

> 🧠 使用 `isSelecting` 来标记是否刚刚执行过选中操作，避免刚选完就触发隐藏。

---

### 4. 工具栏功能按钮绑定

每个按钮都绑定了相应的处理函数：

| 按钮 | 功能 | 对应方法 |
|------|------|----------|
| 查词 | 输出选中内容 | `handleLookup` |
| 批注 | 弹出对话框 | `showAnnotationDialog = true` |
| 收藏 | 切换收藏状态 | `toggleCollect` |
| 取消显示 | 隐藏工具栏 | `showToolbar = false` |

这些方法都很简单，主要是对数据的操作，比如保存批注、切换收藏状态等。

---

## 🧱 四、数据流与响应式管理

使用 Vue 的响应式系统（如 `ref` 和 `reactive`）管理状态：

```js
const showToolbar = ref(false) // 是否显示工具栏
const selectedText = ref('')   // 当前选中的文字
const toolbarPos = reactive({ x: 0, y: 0 }) // 工具栏坐标
const isSelecting = ref(false) // 是否正在选择
```

这些变量的变化都会自动更新视图，实现数据驱动 UI。

---

## 📦 五、组件结构简述

```html
<template>
  <el-container>
    <!-- 左侧文章列表 -->
    <el-aside>...</el-aside>

    <!-- 右侧文章内容 -->
    <el-main>
      <div @mouseup="handleTextSelection"> <!-- 监听选中 -->
        <pre>{{ currentArticle.content }}</pre>
        
        <!-- 浮动工具栏 -->
        <div v-if="showToolbar" :style="..."></div>
      </div>

      <!-- 批注对话框 -->
      <el-dialog v-model="showAnnotationDialog">...</el-dialog>
    </el-main>
  </el-container>
</template>
```

---

## 🎨 六、样式与定位

工具栏使用的是 `position: fixed` 定位，这样即使滚动页面也能固定在屏幕某个位置。

```css
.selection-toolbar {
  position: fixed;
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  border-radius: 4px;
  padding: 4px;
  z-index: 1000;
}
```

---

## ✅ 七、总结：选中功能完整流程图

```
用户拖动鼠标选中文本
       ↓
触发 mouseup 事件
       ↓
调用 handleTextSelection 方法
       ↓
获取选中文字内容和位置
       ↓
设置 selectedText 和 toolbarPos
       ↓
显示浮动工具栏
       ↓
用户点击其他区域
       ↓
触发全局 click 事件
       ↓
判断是否为选中行为后的点击
       ↓
如果不是，则隐藏工具栏
```

---

## 💡 小贴士（可选扩展）

你可以继续扩展以下功能：

| 功能 | 描述 |
|------|------|
| 高亮已选内容 | 使用 `<mark>` 或背景色标记选中部分 |
| 存储历史选区 | 记录用户之前选过的单词或句子 |
| 自动查词典 | 联网查询单词释义并展示 |
| 多语言支持 | 支持英文、日文、韩文等多语言查词 |

---

如果你还有关于 **高亮选中区域**、**记录批注位置** 或 **添加查词API接口** 的问题，也欢迎继续提问，我可以帮你逐步完善这个功能。💪