using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Threading;

namespace ChordDisplay
{
    public class MainViewModel : PropertyChangedBase
    {
        FileSystemWatcher fileWatcher;

        public ObservableCollection<ChordNode> Nodes
        {
            get;
        } = new ObservableCollection<ChordNode>();


        public MainViewModel()
        {
            fileWatcher = new FileSystemWatcher(@"C:\Temp\", "*.csv");
            fileWatcher.Changed += FileWatcher_Changed;
            fileWatcher.EnableRaisingEvents = true;

            UpdateRing();
        }

        private void FileWatcher_Changed(object sender, FileSystemEventArgs e)
        {
            if (e.Name == "AllNodes.csv" && e.ChangeType == WatcherChangeTypes.Changed)
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    UpdateRing();
                });
            }
        }

        private void UpdateRing()
        {
            Nodes.Clear();

            foreach (var node in ReadFile(@"C:\Temp\AllNodes.csv"))
            {
                Nodes.Add(node);
            }
        }

        private IEnumerable<ChordNode> ReadFile(string fullFilename)
        {
            foreach (var line in File.ReadAllLines(fullFilename))
            {
                var split = line.Split(';');

                var hash = split[0];
                var url = split[1];

                yield return new ChordNode() { ID = hash, IP = url };
            }
        }
    }
}
