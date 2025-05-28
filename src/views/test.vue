<script setup>
import myAvatarImg from '@/assets/ai.png'
import defaultAvatarImg from '@/assets/green.png'
import { ref, computed } from 'vue'
import { Avatar, EditPen, Star } from '@element-plus/icons-vue'

// 模拟头像地址
const myAvatar = myAvatarImg
const defaultAvatar = defaultAvatarImg
const currentHistory = computed(() => history.value[selectedIdx.value] || { messages: [], avatar: '', title: '' })
const selectedIdx = ref(0)

const history = ref([
  {
    id: 1,
    title: '小明',
    avatar: defaultAvatar,
    messages: [
      { role: 'me', content: '你好，小明！' },
      { role: 'other', content: '你好呀！' },
      { role: 'me', content: '最近怎么样？' },
      { role: 'other', content: '挺好的，你呢？' }
    ]
  },
  // ...其他数据
])


function selectHistory(idx) {
  selectedIdx.value = Number(idx)
}

function editMessage(idx) {
  // 这里只是入口
  alert('润色功能入口，句子索引：' + idx)
}

function addToCollection(idx) {
  // 这里只是入口
  alert('添加到积累功能入口，句子索引：' + idx)
}
// ...原有脚本逻辑
</script>