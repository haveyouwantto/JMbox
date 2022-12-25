// For top bar & menu

// Top bar Items
let head = document.getElementById("head");
let title = head.querySelector("#title");
let backBtn = head.querySelector("#back");
let homeBtn = head.querySelector("#home");
let menuBtn = head.querySelector("#menu");

let menu = head.querySelector(".menu");
let collapse = document.querySelector("#collapse");

// Menu Items
let wav = document.getElementById("wav");
let mid = document.getElementById("mid");
let dark = document.getElementById("dark");

let refresh = document.getElementById('refresh');


// Misc Items
let metaThemeColor = document.getElementById("meta-theme-color");

// loop.addEventListener('click', function (e) {
//     musicLoop = !musicLoop;
//     audio.loop = musicLoop;
//     if (musicLoop) {
//         loop.classList.remove('button-disabled');
//     } else {
//         loop.classList.add('button-disabled');
//     }
// });

// Top bar back button 
// <
backBtn.addEventListener('click', function (e) {
    back();
});

// Top bar home button
// ⌂
homeBtn.addEventListener('click', function (e) {
    pathman.home();
    list();
});

// menu display style changer
let menuDisplay = false;
function setMenuVisible(visible) {
    menuDisplay = visible;
    let actual = menu.classList.contains('menu-visible');

    if (visible != actual) {
        if (visible) {
            menu.classList.add('menu-visible');
            menu.classList.remove('menu-hidden');
            collapse.classList.remove('hidden');
        } else {
            menu.classList.remove('menu-visible');
            menu.classList.add('menu-hidden');
            collapse.classList.add('hidden');
        }
    }
    menuDisplay = !actual;
}

// Toggle menu button
menuBtn.addEventListener('click', function (e) {
    menuDisplay = !menuDisplay;
    setMenuVisible(menuDisplay);
});

// Close on click outside of the menu
collapse.addEventListener('click', function (e) {
    if (menuDisplay) {
        setMenuVisible(false);
    }
});

// Close on click on menu item
menu.addEventListener('click', e => {
    if (menuDisplay) {
        setMenuVisible(false);
    }
});

// Dark mode
dark.addEventListener('click', e => {
    config.dark = !config.dark;
    setDarkMode(config.dark);
    update('dark', config.dark);
});

function setDarkMode(dark) {
    let root = document.documentElement.style;
    if (dark) {
        root.setProperty('--text-color', '#cccccc');
        root.setProperty('--bg-color', '#101010');
        root.setProperty('--hover-color', '#ffffff20');
        root.setProperty('--bg-color-alt', '#202020');
        root.setProperty('--bar-color', '#303030');
        fillColor = '#101010';
    } else {
        root.setProperty('--text-color', '#202020');
        root.setProperty('--bg-color', 'white');
        root.setProperty('--hover-color', '#00000020');
        root.setProperty('--bg-color-alt', 'white');
        root.setProperty('--bar-color', '#e0e0e0');
        fillColor = 'white';
    }
}

refresh.addEventListener('click', e => {
    list(true);
});

setDarkMode(config.dark);

// TODO: respond to browser back button

// let last = location.hash;
// window.addEventListener('popstate', e => {
//     if (location.hash != last) {
//         goto(location.hash.substring(1));
//     }
//     console.log(last, location.hash);

//     last = location.hash;
//     list('', false)
// });


const openSettingsButton = document.getElementById('open-settings-button');
const closeSettingsButton = document.getElementById('close-settings-button');
const settingsDialog = document.getElementById('settings-dialog');

openSettingsButton.addEventListener('click', () => {
    settingsDialog.classList.remove("fade-out");
    settingsDialog.showModal();
});

closeSettingsButton.addEventListener('click', () => {
    settingsDialog.classList.add("fade-out");
    settingsDialog.close();
});


const dialog = document.getElementById('common-dialog');
const dialogTitle = dialog.querySelector('.title');
const dialogContent = dialog.querySelector('.dialog-container');
const closeDialogButton = document.getElementById('close-dialog-button');

closeDialogButton.addEventListener('click', () => {
    dialog.close();
});

const aboutButton = document.getElementById('about-button');
aboutButton.addEventListener('click', e=>{
    dialogTitle.innerText = 'About';
    dialogContent.innerHTML = '';
    dialogContent.appendChild(createDialogItem('<a href="https://github.com/haveyouwantto/JMBox" class="link">JMBox</a>'));
    dialogContent.appendChild(createDialogItem("(C) 2022 haveyouwantto"));
    dialogContent.appendChild(createDialogItem("Licensed under MIT License."));
    dialogContent.appendChild(createDialogItem("Library Used: "));
    dialogContent.appendChild(createDialogItem('<a href="https://github.com/cagpie/PicoAudio.js" class="link">PicoAudio</a> (MIT License)'));
    dialog.showModal();
});