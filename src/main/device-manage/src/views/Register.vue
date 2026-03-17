<template>
  <div class="register-page">
    <el-card class="register-card">
      <h2 class="title">用户注册</h2>
      <p class="subtitle">实验工具管理平台 · 注册后默认为学生角色</p>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="学号/工号，用于登录" autocomplete="off" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请设置登录密码" show-password autocomplete="off" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="再次输入密码" show-password autocomplete="off" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="真实姓名（选填）" autocomplete="off" />
        </el-form-item>
        <el-form-item label="手机" prop="phone">
          <el-input v-model="form.phone" placeholder="手机号（选填）" autocomplete="off" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="邮箱（选填）" autocomplete="off" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister" class="submit-btn">
            注册
          </el-button>
        </el-form-item>
        <div class="footer-links">
          <router-link to="/login">已有账号？去登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Register',
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (value !== this.form.password) callback(new Error('两次输入的密码不一致'))
      else callback()
    }
    return {
      loading: false,
      form: {
        username: '',
        password: '',
        confirmPassword: '',
        realName: '',
        phone: '',
        email: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码至少 6 位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirm, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    handleRegister() {
      this.$refs.formRef.validate(async (valid) => {
        if (!valid) return
        this.loading = true
        try {
          await axios.post('/api/user/register', {
            username: this.form.username.trim(),
            password: this.form.password,
            realName: this.form.realName?.trim() || undefined,
            phone: this.form.phone?.trim() || undefined,
            email: this.form.email?.trim() || undefined
          })
          this.$message.success('注册成功，请登录')
          this.$router.push('/login')
        } catch (e) {
          const msg = e.response?.data?.msg || '注册失败'
          this.$message.error(msg)
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2b5876, #4e4376);
}
.register-card {
  width: 440px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.15);
}
.title {
  text-align: center;
  margin-bottom: 8px;
  color: #303133;
}
.subtitle {
  text-align: center;
  margin-bottom: 24px;
  font-size: 13px;
  color: #909399;
}
.submit-btn {
  width: 100%;
}
.footer-links {
  text-align: center;
  margin-top: 16px;
}
.footer-links a {
  color: #409eff;
  text-decoration: none;
  font-size: 14px;
}
.footer-links a:hover {
  text-decoration: underline;
}
</style>
