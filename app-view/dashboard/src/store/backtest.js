import {defineStore} from "pinia";

// async function getContent(id) {
//   const response = await axios.get('http://localhost:8080/' + id);
//   return response.data;
// }


// async function getSummary(id) {
//   const response = await axios.get('http://localhost:8080/' + id);
//   return response.data;
// }


export const useNoticeStore = defineStore("backtestStore", {

    state: () => ({
        notice: Object,
        notices: [],
        lastLoadedId: 0,
        loading: false,
    }),


    getters: {},

    actions: {
        async loadData(id) {

        }
    }
});