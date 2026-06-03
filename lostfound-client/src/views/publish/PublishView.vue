<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, shallowRef } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { lostApi } from '@/api/lost'
import { ElMessage } from 'element-plus'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const editId = ref<number>(0)
const saving = ref(false)

const form = ref({
  title: '',
  content: '',
  type: 1,
  category: '',
  contact: '',
  location: '',
  lostDate: '',
  coverImage: '',
  images: '[]'
})

const categories = ['证件', '电子产品', '钥匙', '钱包', '衣物', '其他']

// 简易富文本编辑器 - wangEditor
const editorRef = shallowRef()
const toolbarConfig = {}
const editorConfig = { placeholder: '请详细描述失物/寻物信息...', MENU_CONF: {} }
const mode = 'default'

function handleCreated(editor: any) {
  editorRef.value = editor
}

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor) editor.destroy()
})

// 初始化编辑
async function loadEditData() {
  const id = route.params.id
  if (id) {
    isEdit.value = true
    editId.value = Number(id)
    try {
      const res = await lostApi.getDetail(Number(id))
      const item = res.data.data
      form.value = {
        title: item.title,
        content: item.content,
        type: item.type,
        category: item.category || '',
        contact: item.contact,
        location: item.location || '',
        lostDate: item.lostDate || '',
        coverImage: item.coverImage || '',
        images: item.images || '[]'
      }
    } catch { router.push('/') }
  }
}

async function handleSubmit() {
  if (!form.value.title || !form.value.content || !form.value.contact) {
    ElMessage.warning('请填写标题、详细描述和联系方式')
    return
  }
  saving.value = true
  try {
    if (isEdit.value) {
      await lostApi.update(editId.value, form.value)
      ElMessage.success('更新成功，需重新审核')
    } else {
      await lostApi.publish(form.value)
      ElMessage.success('发布成功，请等待审核')
    }
    router.push('/user/posts')
  } catch { /* handled */ } finally {
    saving.value = false
  }
}

onMounted(() => loadEditData())
</script>

<template>
  <div style="max-width:800px;margin:0 auto;">
    <h2 class="page-title">{{ isEdit ? '编辑信息' : '发布失物/寻物信息' }}</h2>

    <el-form label-width="80px" style="background:#fff;padding:24px;border-radius:8px;">
      <el-form-item label="类型" required>
        <el-radio-group v-model="form.type">
          <el-radio :value="1">🔍 失物招领（捡到东西找失主）</el-radio>
          <el-radio :value="2">📢 寻物启事（丢了东西找物品）</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="标题" required>
        <el-input v-model="form.title" placeholder="例如：在食堂捡到黑色钱包" maxlength="200" show-word-limit />
      </el-form-item>

      <el-form-item label="分类">
        <el-select v-model="form.category" placeholder="选择物品分类" clearable>
          <el-option v-for="c in categories" :key="c" :value="c" :label="c" />
        </el-select>
      </el-form-item>

      <el-form-item label="详细描述" required>
        <div style="border:1px solid #dcdfe6;border-radius:4px;width:100%;">
          <Toolbar style="border-bottom:1px solid #dcdfe6;" :editor="editorRef" :defaultConfig="toolbarConfig" :mode="mode" />
          <Editor style="height:400px;overflow-y:hidden;" v-model="form.content" :defaultConfig="editorConfig" :mode="mode" @onCreated="handleCreated" />
        </div>
      </el-form-item>

      <el-form-item label="联系方式" required>
        <el-input v-model="form.contact" placeholder="手机号/微信号，方便联系" maxlength="200" />
      </el-form-item>

      <el-form-item label="丢失地点">
        <el-input v-model="form.location" placeholder="如：图书馆二楼" />
      </el-form-item>

      <el-form-item label="丢失日期">
        <el-date-picker v-model="form.lostDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="saving" @click="handleSubmit">
          {{ isEdit ? '保存修改' : '发布信息' }}
        </el-button>
        <el-button @click="router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
