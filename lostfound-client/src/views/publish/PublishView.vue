<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, shallowRef } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { lostApi } from '@/api/lost'
import { uploadApi } from '@/api/user'
import { ElMessage } from 'element-plus'
import type { UploadFile } from 'element-plus'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'
import PageHeader from '@/components/PageHeader.vue'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const editId = ref<number>(0)
const saving = ref(false)
// 图片上传状态
const uploadingCover = ref(false)
const uploadingImages = ref(false)
const imageList = ref<string[]>([])
const coverImageUrl = ref('')

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

// wangEditor 配置
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

// 加载编辑数据
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

// 封面图：选择文件后通过 axios 手动上传
async function onCoverChange(file: UploadFile) {
  const rawFile = file.raw
  if (!rawFile) return
  // 校验
  const mimeType = rawFile.type || ''
  const fileName = rawFile.name || file.name || ''
  const isImage = mimeType.startsWith('image/') || /\.(jpg|jpeg|png|gif|webp|bmp)$/i.test(fileName)
  if (!isImage) { ElMessage.error('只能上传图片文件'); return }
  if (rawFile.size / 1024 / 1024 > 10) { ElMessage.error('图片大小不能超过 10MB'); return }

  uploadingCover.value = true
  try {
    const res = await uploadApi.uploadImage(rawFile)
    const url = res.data?.data
    if (url) {
      form.value.coverImage = url
      coverImageUrl.value = url
      ElMessage.success('封面上传成功')
    } else {
      ElMessage.error('封面上传失败：返回数据异常')
    }
  } catch (e: any) {
    console.error('封面上传失败', e)
    ElMessage.error('封面上传失败')
  }
  uploadingCover.value = false
}

// 多图：选择文件后通过 axios 手动上传
async function onImageChange(file: UploadFile) {
  const rawFile = file.raw
  if (!rawFile) return
  const mimeType = rawFile.type || ''
  const fileName = rawFile.name || file.name || ''
  const isImage = mimeType.startsWith('image/') || /\.(jpg|jpeg|png|gif|webp|bmp)$/i.test(fileName)
  if (!isImage) { ElMessage.error('只能上传图片文件'); return }
  if (rawFile.size / 1024 / 1024 > 10) { ElMessage.error('图片大小不能超过 10MB'); return }

  uploadingImages.value = true
  try {
    const res = await uploadApi.uploadImage(rawFile)
    const url = res.data?.data
    if (url) {
      imageList.value.push(url)
      form.value.images = JSON.stringify(imageList.value)
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error('图片上传失败：返回数据异常')
    }
  } catch (e: any) {
    console.error('图片上传失败', e)
    ElMessage.error('图片上传失败')
  }
  uploadingImages.value = false
}

// 移除已上传图片
function removeImage(index: number) {
  imageList.value.splice(index, 1)
  form.value.images = JSON.stringify(imageList.value)
}

// 移除封面图
function removeCover() {
  coverImageUrl.value = ''
  form.value.coverImage = ''
}

onMounted(() => loadEditData())
</script>

<template>
  <div class="publish-page">
    <PageHeader
      :title="isEdit ? '编辑信息' : '发布信息'"
      :subtitle="isEdit ? '修改已发布的信息' : '填写以下信息，发布失物招领或寻物启事'"
      :show-back="true"
      @back="router.back()"
    />

    <div class="publish-card">
      <el-form label-position="top" class="publish-form">
        <!-- 信息类型 -->
        <div class="form-section">
          <h3 class="form-section-title">信息类型</h3>
          <el-radio-group v-model="form.type" class="type-radio-group">
            <el-radio-button :value="1" class="type-radio-btn">
              <div class="type-option">
                <el-icon :size="24"><Search /></el-icon>
                <span class="type-option-label">失物招领</span>
                <small class="type-option-desc">捡到物品寻找失主</small>
              </div>
            </el-radio-button>
            <el-radio-button :value="2" class="type-radio-btn">
              <div class="type-option">
                <el-icon :size="24"><WarningFilled /></el-icon>
                <span class="type-option-label">寻物启事</span>
                <small class="type-option-desc">丢失物品寻求帮助</small>
              </div>
            </el-radio-button>
          </el-radio-group>
        </div>

        <!-- 基本信息 -->
        <div class="form-section">
          <h3 class="form-section-title">基本信息</h3>
          <el-form-item label="标题">
            <el-input v-model="form.title" placeholder="例如：在食堂捡到黑色钱包" maxlength="200" show-word-limit />
          </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="form.category" placeholder="选择物品分类" clearable class="form-select">
              <el-option v-for="c in categories" :key="c" :value="c" :label="c" />
            </el-select>
          </el-form-item>
        </div>

        <!-- 详细描述 -->
        <div class="form-section">
          <h3 class="form-section-title">详细描述</h3>
          <el-form-item label="详情内容（支持富文本编辑）">
            <div class="editor-wrap">
              <Toolbar
                style="border-bottom:1px solid #dcdfe6;"
                :editor="editorRef"
                :defaultConfig="toolbarConfig"
                :mode="mode"
              />
              <Editor
                style="height:400px;overflow-y:hidden;"
                v-model="form.content"
                :defaultConfig="editorConfig"
                :mode="mode"
                @onCreated="handleCreated"
              />
            </div>
          </el-form-item>
        </div>

        <!-- 联系方式 -->
        <div class="form-section">
          <h3 class="form-section-title">联系方式</h3>
          <el-form-item label="联系电话/微信号">
            <el-input v-model="form.contact" placeholder="手机号/微信号，方便失主或捡到者联系您" maxlength="200" />
          </el-form-item>
          <div class="form-row">
            <el-form-item label="丢失/捡到地点" class="form-flex-item">
              <el-input v-model="form.location" placeholder="如：图书馆二楼" />
            </el-form-item>
            <el-form-item label="丢失/捡到日期" class="form-flex-item">
              <el-date-picker
                v-model="form.lostDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                class="form-datepicker"
              />
            </el-form-item>
          </div>
        </div>

        <!-- 图片上传 -->
        <div class="form-section">
          <h3 class="form-section-title">图片上传</h3>
          <!-- 封面图 -->
          <el-form-item label="封面图片">
            <div class="upload-area">
              <div v-if="coverImageUrl" class="cover-preview">
                <img :src="coverImageUrl" alt="封面图" />
                <el-button type="danger" size="small" circle class="cover-remove-btn" @click="removeCover">
                  <el-icon><Close /></el-icon>
                </el-button>
              </div>
              <el-upload
                v-else
                :show-file-list="false"
                :auto-upload="false"
                :on-change="onCoverChange"
                accept="image/*"
                drag
              >
                <el-icon :size="36"><UploadFilled /></el-icon>
                <div class="upload-text">点击或拖拽上传封面图</div>
              </el-upload>
            </div>
          </el-form-item>

          <!-- 多图 -->
          <el-form-item label="更多图片">
            <div v-if="imageList.length > 0" class="image-preview-list">
              <div v-for="(url, i) in imageList" :key="i" class="image-preview-item">
                <img :src="url" alt="图片" />
                <el-button type="danger" size="small" circle class="image-remove-btn" @click="removeImage(i)">
                  <el-icon><Close /></el-icon>
                </el-button>
              </div>
            </div>
            <el-upload
              :show-file-list="false"
              :auto-upload="false"
              :on-change="onImageChange"
              accept="image/*"
            >
              <el-button :loading="uploadingImages">
                <el-icon><Plus /></el-icon> 添加图片
              </el-button>
            </el-upload>
          </el-form-item>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <el-button type="primary" size="large" :loading="saving" @click="handleSubmit">
            {{ isEdit ? '保存修改' : '发布信息' }}
          </el-button>
          <el-button size="large" @click="router.back()">取消</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.publish-page {
  max-width: 760px;
  margin: 0 auto;
}

.publish-card {
  background: var(--bg-white, #fff);
  border: 1px solid var(--border-base, #e8e8e8);
  border-radius: var(--radius-md, 4px);
  padding: 32px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.04));
}

/* 分区 */
.form-section {
  margin-bottom: 28px;
  padding-bottom: 28px;
  border-bottom: 1px solid var(--border-light, #f0f0f0);
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.form-section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary, #333);
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-section-title::before {
  content: '';
  display: inline-block;
  width: 3px;
  height: 14px;
  background: var(--primary, #00A884);
  border-radius: 2px;
}

/* 类型选择 */
.type-radio-group {
  display: flex;
  gap: 16px;
  width: 100%;
}

.type-radio-btn {
  flex: 1;
}

.type-radio-btn :deep(.el-radio-button__inner) {
  width: 100%;
  padding: 20px 16px;
  height: auto;
  border-radius: var(--radius-md, 4px) !important;
  border: 1px solid var(--border-base, #dcdfe6) !important;
  transition: all 0.2s;
}

.type-radio-btn :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  border-color: var(--primary, #00A884) !important;
  background: rgba(0, 168, 132, 0.05);
  color: var(--primary, #00A884);
  box-shadow: none;
}

.type-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.type-option-label {
  font-size: 15px;
  font-weight: 600;
}

.type-option-desc {
  font-size: 12px;
  color: var(--text-secondary, #999);
}

/* 表单控件 */
.form-select {
  width: 280px;
}

.editor-wrap {
  border: 1px solid var(--border-base, #dcdfe6);
  border-radius: var(--radius-md, 4px);
  overflow: hidden;
  width: 100%;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-flex-item {
  flex: 1;
}

.form-datepicker {
  width: 100%;
}

/* 提交按钮区 */
.form-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  padding-top: 8px;
}

/* 图片上传 */
.upload-area {
  width: 100%;
}

.cover-preview {
  position: relative;
  display: inline-block;
  max-width: 260px;
}

.cover-preview img {
  width: 100%;
  max-height: 180px;
  object-fit: cover;
  border-radius: var(--radius-md, 4px);
  border: 1px solid var(--border-base, #e8e8e8);
}

.cover-remove-btn {
  position: absolute;
  top: -8px;
  right: -8px;
}

.upload-text {
  font-size: 13px;
  color: var(--text-secondary, #999);
  margin-top: 6px;
}

.image-preview-list {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}

.image-preview-item {
  position: relative;
  width: 100px;
  height: 100px;
}

.image-preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: var(--radius-md, 4px);
  border: 1px solid var(--border-base, #e8e8e8);
}

.image-remove-btn {
  position: absolute;
  top: -6px;
  right: -6px;
}

/* 响应式 */
@media (max-width: 768px) {
  .publish-card {
    padding: 20px 16px;
  }

  .type-radio-group {
    flex-direction: column;
    gap: 10px;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .form-select {
    width: 100%;
  }
}
</style>
