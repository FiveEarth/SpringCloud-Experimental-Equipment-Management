<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2 class="title">实验工具管理平台</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" show-password autocomplete="off" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" class="login-btn">
            登录
          </el-button>
        </el-form-item>
        <div class="footer-links">
          <router-link to="/register">没有账号？去注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'
import { setLoginData, getRoles } from '../utils/auth'

export default {
  name: 'Login',
  data() {
    return {
      loading: false,
      form: {
        username: '',
        password: ''
      },
      rules: {
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    handleLogin() {
      this.$refs.formRef.validate(async (valid) => {
        if (!valid) return
        this.loading = true
        try {
          const resp = await axios.post('/api/user/login', this.form)
          if (resp.data && resp.data.code === 200) {
            const data = resp.data.data || {}
            if (data.token) {
              setLoginData({
                token: data.token,
                userId: data.userId,
                realName: data.realName,
                username: data.username,
                roles: data.roles,
                permissions: data.permissions
              })
            }
            const roles = getRoles()
            if (roles.includes('ADMIN')) this.$router.push('/user-manage')
            else if (roles.includes('TEACHER')) this.$router.push('/apply-approve')
            else if (roles.includes('REPAIR')) this.$router.push('/repairer-dashboard')
            else this.$router.push('/dashboard')
          } else {
            this.$message.error(resp.data?.msg || '登录失败')
          }
        } catch (e) {
          this.$message.error('登录失败')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2b5876, #4e4376);
}
.login-card {
  width: 380px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.15);
}
.title {
  text-align: center;
  margin-bottom: 24px;
  color: #303133;
  font-size: 20px;
}
.login-btn {
  width: 100%;
}
.footer-links {
  text-align: center;
  margin-top: 12px;
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

