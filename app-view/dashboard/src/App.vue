<template>
    <v-layout>
        <v-navigation-drawer color="#181B1F" permanent>
            <NavbarView />
        </v-navigation-drawer>

        <v-main>
            <v-container>
                <router-view/>
            </v-container>
            
        </v-main>

     
    </v-layout>

</template>

<script>
import NavbarView from './views/NavbarView.vue'

export default {
    name: 'App',
    components: {
        NavbarView
    }
}
</script>
