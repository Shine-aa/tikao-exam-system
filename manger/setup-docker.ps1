# Docker 配置脚本
# 用途：一键拉取代码执行所需的 Docker 镜像

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Docker 镜像配置脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查 Docker 是否运行
Write-Host "1. 检查 Docker 是否运行..." -ForegroundColor Yellow
try {
    $dockerVersion = docker --version 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✓ Docker 已安装: $dockerVersion" -ForegroundColor Green
    } else {
        Write-Host "   ✗ Docker 未安装或未运行" -ForegroundColor Red
        Write-Host "   请先启动 Docker Desktop" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "   ✗ Docker 未安装或未运行" -ForegroundColor Red
    Write-Host "   请先启动 Docker Desktop" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "2. 测试 Docker 连接..." -ForegroundColor Yellow
try {
    docker ps > $null 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✓ Docker 连接正常" -ForegroundColor Green
    } else {
        Write-Host "   ✗ Docker 无法连接" -ForegroundColor Red
        Write-Host "   请检查 Docker Desktop 是否已启动" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "   ✗ Docker 无法连接" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "3. 检查镜像是否已存在..." -ForegroundColor Yellow

$images = docker images --format "{{.Repository}}:{{.Tag}}" 2>&1

$imageList = @(
    @{ Name = "openjdk:17-jdk-slim"; Desc = "Java 17" },
    @{ Name = "python:3.11-slim"; Desc = "Python 3.11" },
    @{ Name = "gcc:latest"; Desc = "GCC (C++)" }
)

$imagesToPull = @()

foreach ($img in $imageList) {
    if ($images -match [regex]::Escape($img.Name)) {
        Write-Host "   ✓ $($img.Desc) 镜像已存在: $($img.Name)" -ForegroundColor Green
    } else {
        Write-Host "   ✗ $($img.Desc) 镜像不存在: $($img.Name)" -ForegroundColor Yellow
        $imagesToPull += $img
    }
}

Write-Host ""
if ($imagesToPull.Count -eq 0) {
    Write-Host "所有镜像已存在，无需下载！" -ForegroundColor Green
    Write-Host ""
    Write-Host "当前镜像列表：" -ForegroundColor Cyan
    docker images | Select-String "openjdk|python|gcc"
    exit 0
}

Write-Host "4. 开始拉取缺失的镜像..." -ForegroundColor Yellow
Write-Host "   （这可能需要几分钟，取决于网速）" -ForegroundColor Gray
Write-Host ""

foreach ($img in $imagesToPull) {
    Write-Host "   正在拉取: $($img.Desc) ($($img.Name))..." -ForegroundColor Cyan
    try {
        docker pull $img.Name
        if ($LASTEXITCODE -eq 0) {
            Write-Host "   ✓ $($img.Desc) 下载成功" -ForegroundColor Green
        } else {
            Write-Host "   ✗ $($img.Desc) 下载失败" -ForegroundColor Red
        }
    } catch {
        Write-Host "   ✗ $($img.Desc) 下载失败: $_" -ForegroundColor Red
    }
    Write-Host ""
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  配置完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "当前已下载的镜像：" -ForegroundColor Cyan
docker images | Select-String "openjdk|python|gcc"
Write-Host ""

# 验证镜像
Write-Host "验证镜像..." -ForegroundColor Yellow
$allExist = $true
foreach ($img in $imageList) {
    $exists = docker images --format "{{.Repository}}:{{.Tag}}" | Select-String -Pattern [regex]::Escape($img.Name)
    if ($exists) {
        Write-Host "  ✓ $($img.Desc): $($img.Name)" -ForegroundColor Green
    } else {
        Write-Host "  ✗ $($img.Desc): $($img.Name) 不存在" -ForegroundColor Red
        $allExist = $false
    }
}

Write-Host ""
if ($allExist) {
    Write-Host "✅ 所有镜像已就绪！" -ForegroundColor Green
    Write-Host ""
    Write-Host "下一步：" -ForegroundColor Cyan
    Write-Host "1. 确保 Docker Desktop 已启用 TCP 端口 (Settings → General)" -ForegroundColor White
    Write-Host "2. 确认后端配置 (application.yml): host: tcp://localhost:2375" -ForegroundColor White
    Write-Host "3. 启动后端应用并测试代码执行功能" -ForegroundColor White
} else {
    Write-Host "⚠️  部分镜像下载失败，请检查网络连接后重试" -ForegroundColor Yellow
}

