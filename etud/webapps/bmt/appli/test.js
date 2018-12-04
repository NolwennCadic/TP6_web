/* Base URL of the web-service for the current user */
var wsBase = '/bmt/tata/'

//http://serveur/bmt/<User>/clean?x-http-method=post
// On teste la fonction clean pour voir si tous les bookmarks sont éffacés

async function test_clean(){
  var url = wsBase + "clean";
  var x = await $.post(url)
  url = wsBase + "tags";
  x = await $.get(url);
  if (x == null) {
          $("ul#tags").append("<li class='passed'>la fonction test_clean a reussi </li>")
  }
  else{
          $("ul#tags").append("<li class='failed'>la fonction test_clean a échoué </li>")
  }
}

async function test_reinit(){
        var url = wsBase + "reinit";
        var x = await $.post(url);
        url = wsBase + "tags"
        x = await $.get(url)
        if (x != null) {
                 $("ul#tags").append("<li class='passed'> La fonction test_reinit a reussi </li>")
        }
        else{
                 $("ul#tags").append("<li class='failed'> La fonction test_reinit a échoué</li>")
        }
}

async function test_modify_tag(){
        var  idTag = 2 ;
        var url = wsBase + "tags/"
        var x = await $.get(url)

}


$(function() {
        test_clean();
        test_reinit();
})

/*
http://localhost:8080/bmt/tata/clean?x-http-method=post
*/
