<template>
  <nav class="navbar is-primary">
    <div class="navbar-brand">
      <div class="navbar-item">Cash.x</div>
    </div>
    <div class="navbar-menu">
      <div class="navbar-left">
        <router-link :to="{name: 'Operations'}">Operations</router-link>
        <router-link :to="{name: 'Categories'}">Categories</router-link>
        <router-link :to="{name: 'Accounts'}">Accounts</router-link>
      </div>
    </div>
  </nav>

  <section class="section">
    <div class="container">
      <router-view></router-view>
    </div>
  </section>
</template>

