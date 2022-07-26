<template>
    <div class="loading-container">
        <a-spin tip="Loading..."></a-spin>
    </div>
</template>

<style lang="less" scoped>
.loading-container {
    position: fixed;
    width: 100vw;
    height: 100vh;
    top: 0;
    left: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: rgba(255, 255, 255, .5);
    z-index: 9999;
}
</style>